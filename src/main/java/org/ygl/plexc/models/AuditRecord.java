package org.ygl.plexc.models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.CreatedTimestamp;

/**
 *
 * @author ylegall
 *
 */
@Entity
@Table(name = "AuditLog")
public class AuditRecord {

	@Column(nullable = false)
	String issuer;

	@Column(nullable = false)
	String target;

	@Column(nullable = false)
	boolean success;

	double lat;

	double lng;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	@CreatedTimestamp
	Timestamp timestamp;

	/**
	 * default constructor required by ebean.
	 */
	public AuditRecord() {
		super();
	}

	/**
	 * Constructor using issuer target and success. Timestamp is automatic.
	 *
	 * @param issuer
	 * @param target
	 * @param success
	 */
	public AuditRecord(String issuer, String target, boolean success) {
		super();
		this.issuer = issuer;
		this.target = target;
		this.success = success;
	}

	/**
	 *
	 * @param issuer
	 * @param target
	 * @param timestamp
	 * @return
	 */
	public static int accessCount(String target, Timestamp timestamp) {
		return accessCount(target, timestamp, true);
	}

	/**
	 *
	 * @param issuer
	 * @param target
	 * @param timestamp
	 * @return
	 */
	public static int accessCount(String target, Timestamp timestamp, boolean success) {
		List<AuditRecord> records = Ebean.find(AuditRecord.class)
				.where()
				.eq("target", target)
				.eq("success", success)
				.gt("timestamp", timestamp)
				.findList();
		return records.size();
	}

	/**
	 *
	 * @param issuer
	 * @param target
	 * @param timestamp
	 * @return
	 */
	public static int accessCount(String issuer, String target, Timestamp timestamp) {
		return accessCount(issuer, target, timestamp, true);
	}

	/**
	 *
	 * @param issuer
	 * @param target
	 * @param timestamp
	 * @return
	 */
	public static int accessCount(String issuer, String target, Timestamp timestamp, boolean success) {
		Ebean.beginTransaction();
		List<AuditRecord> records = Ebean.find(AuditRecord.class)
				.where()
				.eq("issuer", issuer)
				.eq("target", target)
				.eq("success", success)
				.gt("timestamp", timestamp)
				.findList();
		Ebean.endTransaction();
		return records.size();
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

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return String.format(
				"AuditRecord(issuer='%s', target='%s', success='%s')",
				issuer,
				target,
				success);
	}
}
