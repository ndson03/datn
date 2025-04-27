package com.ndson03.quanlykhoahoc.entity;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
@Entity
@Table(name="assignment")
public class Assignment implements Comparable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message="is required")
    @Size(min=1, message="is required")
    @Column(name="name")
    private String name;

    @NotBlank(message="is required")
    @Size(min=1, message="is required")
    @Column(name="description")
    private String description;

    @NotEmpty(message="is required")
    @Column(name="due_date")
    private String dueDate;

    @Column(name="days_remaining")
    private int daysRemaining;

    @Column(name="is_quiz")
    private boolean isQuiz;

    @Column(name="total_score")
    private double totalScore;

    @ManyToMany
    @JoinTable(name="assignment_details",
            joinColumns = @JoinColumn(name="assignment_id"),
            inverseJoinColumns = @JoinColumn(name="student_course_details_id"))
    private List<StudentCourseDetails> courseDetails;

    @OneToMany(mappedBy = "assignment")
    private List<QuizQuestion> questions;

    public Assignment() {
        this.totalScore = 10.0; // Mặc định 10 điểm
    }

    public Assignment(int id, String name, String description, String dueDate, int daysRemaining,
                      List<StudentCourseDetails> courseDetails) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.daysRemaining = daysRemaining;
        this.courseDetails = courseDetails;
        this.totalScore = 10.0; // Mặc định 10 điểm
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDueDate() {
        return dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
    public int getDaysRemaining() {
        return daysRemaining;
    }
    public void setDaysRemaining(int daysRemaining) {
        this.daysRemaining = daysRemaining;
    }
    public List<StudentCourseDetails> getCourseDetails() {
        return courseDetails;
    }
    public void setCourseDetails(List<StudentCourseDetails> courseDetails) {
        this.courseDetails = courseDetails;
    }

    public boolean isQuiz() {
        return isQuiz;
    }

    public void setQuiz(boolean isQuiz) {
        this.isQuiz = isQuiz;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestion> questions) {
        this.questions = questions;
    }

    // Tính điểm cho mỗi câu hỏi
    public double getPointsPerQuestion() {
        if (questions == null || questions.isEmpty()) {
            return 0;
        }
        return totalScore / questions.size();
    }

    @Override
    public int compareTo(Object o) {
        Assignment comAss = (Assignment) o;
        if(this.daysRemaining != comAss.getDaysRemaining()) {
            return this.daysRemaining - comAss.getDaysRemaining();
        } else {
            return this.name.compareTo(comAss.getName());
        }
    }
}