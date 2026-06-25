import jakarta.persistence.*;

@Entity
@Table(name = "department")
public class Department {
	@Id
	@Column(name = "id")
	private Integer id;
	@Column(name = "name")
	private String name;
}
