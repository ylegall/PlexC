package org.ygl.plexc.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;

/**
 * User entity managed by Ebean
 * http://stackoverflow.com/questions/12171243/how-to-share-models-with-non-play-application
 */
@Entity
@Table(name = "users")
public class User {

	@Id
	@Column(nullable=false)
	public String email;

	@Column(nullable=false)
	public String password;

	@Column(nullable=false)
	public String salt;

	@Column(columnDefinition = "TEXT")
	public String policy;

	//private static Query<User> find = Ebean.find(User.class);

	/**
	 * public constructor required by ebean.
	 */
	public User() {
		super();
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public static String getName(User user) {
		return user.email.substring(0, user.email.indexOf('@'));
	}

	/**
	 * Retrieve a User from email.
	 * @param email
	 * @return
	 */
	public static User findByEmail(String email) {
		return Ebean.find(User.class).where().eq("email", email).findUnique();
	}

	/**
	 *
	 * @param user
	 */
	public static void save(User user) {
		Ebean.save(user);
	}

	/**
	 *
	 * @param user
	 * @param policy
	 */
	public static void updatePolicy(User user, String policy) {
		user.setPolicy(policy);
		Ebean.save(user);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	@Override
	public String toString() {
		return "User(" + email + ")";
	}

}
