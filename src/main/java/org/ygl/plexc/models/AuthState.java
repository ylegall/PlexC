package org.ygl.plexc.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "State")
public class AuthState {

	@Id
	@Column(nullable = false)
	public String issuer;

	@Id
	@Column(nullable = false)
	public String target;

	@Lob
	@Column(columnDefinition = "TEXT")
	public String args;

	/**
	 * default constructor required by ebean.
	 */
	public AuthState() {
		super();
	}

	/**
	 * Constructor.
	 * @param owner
	 * @param target
	 * @param args
	 */
	public AuthState(String owner, String target, String args) {
		super();
		this.issuer = owner;
		this.target = target;
		this.args = args;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

}
