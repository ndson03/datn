package com.ndson03.quanlykhoahoc.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import com.ndson03.quanlykhoahoc.domain.validation.FieldMatch;
import com.ndson03.quanlykhoahoc.domain.entity.Role;

@FieldMatch.List({
		@FieldMatch(first = "password", second = "confirmPassword",
				message = "Mật khẩu và xác nhận mật khẩu phải trùng khớp")
})
public class UserDTO {

	@NotBlank(message = "Tên người dùng không được để trống")
	@Size(min = 3, max = 50, message = "Tên người dùng phải từ {min} đến {max} ký tự")
	@Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$", message = "Tên người dùng chỉ được chứa chữ cái, số và các ký tự đặc biệt ._-")
	private String userName;

	@NotBlank(message = "Mật khẩu không được để trống")
	@Size(min = 8, max = 100, message = "Mật khẩu phải từ {min} đến {max} ký tự")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
			message = "Mật khẩu phải chứa ít nhất 1 chữ số, 1 chữ thường, 1 chữ hoa, 1 ký tự đặc biệt và không có khoảng trắng")
	private String password;

	@NotBlank(message = "Xác nhận mật khẩu không được để trống")
	private String confirmPassword;

	@NotBlank(message = "Họ đệm không được để trống")
	@Size(min = 1, max = 50, message = "Họ đệm phải từ {min} đến {max} ký tự")
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "Họ đệm chỉ được chứa chữ cái và các ký tự .'- ")
	private String firstName;

	@NotBlank(message = "Tên không được để trống")
	@Size(min = 1, max = 50, message = "Tên phải từ {min} đến {max} ký tự")
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "Tên chỉ được chứa chữ cái và các ký tự .'- ")
	private String lastName;

	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	@Size(max = 100, message = "Email không được vượt quá {max} ký tự")
	private String email;

	@NotBlank(message = "Số điện thoại không được để trống")
	@Pattern(regexp = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$",
			message = "Số điện thoại không hợp lệ")
	private String phoneNumber;

	@NotNull(message = "Ngày sinh không được để trống")
	@Past(message = "Ngày sinh phải là ngày trong quá khứ")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthDate;

	private Role role;

	public UserDTO() {
		// Constructor mặc định
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}