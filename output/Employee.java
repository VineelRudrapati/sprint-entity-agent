import jakarta.persistence.*;

@Entity
@Table(name = "employee")
public class Employee {
	@Id
	@Column(name = "id")
	private Integer id;
	@Column(name = "name")
	private String name;
	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;
}
