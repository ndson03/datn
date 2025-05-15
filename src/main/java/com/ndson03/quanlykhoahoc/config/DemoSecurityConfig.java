package com.ndson03.quanlykhoahoc.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.domain.entity.Teacher;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
import com.ndson03.quanlykhoahoc.service.user.TeacherService;

@Configuration
@EnableWebSecurity
public class DemoSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private StudentService studentService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//student and teacher login credentials are stored in mysql db, while admin username and password defined below as in-memory

		auth
				.userDetailsService(studentService)
				.passwordEncoder(passwordEncoder());
		auth
				.userDetailsService(teacherService)
				.passwordEncoder(passwordEncoder());

		auth.inMemoryAuthentication()  //admin password username
				.withUser("admin")
				.password(passwordEncoder().encode("1"))
				.roles("ADMIN");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.authorizeRequests()
				.antMatchers("/api/qna/**").permitAll()
				.antMatchers("/").authenticated()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/student/**").hasRole("STUDENT")
				.antMatchers("/teacher/**").hasRole("TEACHER")
				.and()
				.formLogin()
				.loginPage("/showLoginPage")
				.loginProcessingUrl("/authenticateTheUser")
				.successHandler(customAuthenticationSuccessHandler)
				.permitAll()
				.and()
				.logout()
				.logoutUrl("/logout")
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.logoutSuccessUrl("/showLoginPage?logout")
				.permitAll()
				.and()
				.sessionManagement()
				.sessionFixation().migrateSession()  // Bảo vệ phiên khỏi fixation attacks
				.maximumSessions(1)                  // Mỗi người dùng chỉ được phép đăng nhập 1 phiên
				.maxSessionsPreventsLogin(false)     // False: phiên cũ sẽ bị đăng xuất nếu đăng nhập ở nơi khác
				.expiredUrl("/showLoginPage?expired") // Chuyển hướng khi phiên hết hạn
				.and()
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				.invalidSessionUrl("/showLoginPage?invalid") // Chuyển hướng khi phiên không hợp lệ
				.and()
				.exceptionHandling().accessDeniedPage("/access-denied");

		// Thêm bộ lọc custom cho trang đăng nhập
		http.addFilterBefore(new CustomLoginPageFilter(studentService, teacherService), UsernamePasswordAuthenticationFilter.class);
	}

	// Cấu hình thời gian tồn tại của phiên
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	// Định nghĩa thời gian tồn tại của phiên (30 phút)
	@Bean
	public ServletListenerRegistrationBean<HttpSessionEventPublisher> sessionListenerWithMetrics() {
		ServletListenerRegistrationBean<HttpSessionEventPublisher> listenerRegBean =
				new ServletListenerRegistrationBean<>();
		listenerRegBean.setListener(new HttpSessionEventPublisher());
		return listenerRegBean;
	}

	//needed for admin password encoding for security purposes
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Lớp filter để chuyển hướng người dùng đã đăng nhập khi họ truy cập vào trang đăng nhập
	public class CustomLoginPageFilter extends OncePerRequestFilter {

		private final StudentService studentService;
		private final TeacherService teacherService;

		public CustomLoginPageFilter(StudentService studentService, TeacherService teacherService) {
			this.studentService = studentService;
			this.teacherService = teacherService;
		}

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
										FilterChain filterChain) throws ServletException, IOException {
			// Kiểm tra nếu request đến trang đăng nhập và người dùng đã xác thực
			if (request.getRequestURI().equals(request.getContextPath() + "/showLoginPage") &&
					SecurityContextHolder.getContext().getAuthentication() != null &&
					SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
					!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {

				// Lấy thông tin xác thực
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				String role = auth.getAuthorities().iterator().next().toString();
				String userName = auth.getName();

				// Điều hướng tương ứng với vai trò người dùng
				if (role.equals("ROLE_STUDENT")) {
					Student theStudent = studentService.findByStudentName(userName);
					int userId = theStudent.getId();

					// Lưu thông tin sinh viên vào session
					HttpSession session = request.getSession();
					session.setAttribute("user", theStudent);

					response.sendRedirect(request.getContextPath() + "/student/" + userId + "/courses");
					return;
				}
				else if (role.equals("ROLE_TEACHER")) {
					Teacher theTeacher = teacherService.findByTeacherName(userName);
					int userId = theTeacher.getId();

					// Lưu thông tin giáo viên vào session
					HttpSession session = request.getSession();
					session.setAttribute("user", theTeacher);

					response.sendRedirect(request.getContextPath() + "/teacher/" + userId + "/courses");
					return;
				}
				else { // ROLE_ADMIN
					response.sendRedirect(request.getContextPath() + "/admin/adminPanel");
					return;
				}
			}

			// Nếu không phải request đến trang đăng nhập hoặc người dùng chưa đăng nhập,
			// thì để request đi qua chuỗi filter tiếp theo
			filterChain.doFilter(request, response);
		}
	}
}