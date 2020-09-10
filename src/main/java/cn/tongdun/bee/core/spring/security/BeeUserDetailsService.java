package cn.tongdun.bee.core.spring.security;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class BeeUserDetailsService extends JdbcDaoSupport
		implements UserDetailsService {

	private String userTableName;

	private String userIdColumnName = "username";

	private String cnNameColumnName = "username";

	private String queryUserSql;

	@Override
	protected void initDao() throws Exception {
		Assert.notNull(userTableName, "userTableName cannot be null");

		queryUserSql = "select " + userIdColumnName + " as username, " + cnNameColumnName +
				" as cn_name, password, email, admin, sec_enabled" +
				", sec_account_expired_date, sec_credentials_expired_date" +
				", sec_locked_date, sec_non_update_pw, sec_attempts, sec_confirm " +
				"from " + userTableName + " where " + userIdColumnName + " = ?";
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<LoginUserDetails> users = loadUsersByUsername(username);
		if (users.size() == 0) {
			this.logger.debug("Query returned no results for user '" + username + "'");
			throw new UsernameNotFoundException("账号 " + username + " 不存在");
		}

		UserDetails user = users.get(0);
		return user;
	}

	public void updateLoginFailure(UserDetails userDetails) {
		String username = userDetails.getUsername();
		String sql = "update " + userTableName +
				" set sec_attempts = sec_attempts + 1 where " + userIdColumnName + " = ?";

		getJdbcTemplate().update(sql, username);
	}

	public void lockUser(UserDetails userDetails, int lockUserHour) {
		String username = userDetails.getUsername();
		Date date = new Date();
		date = DateUtils.addHours(date, lockUserHour);

		String sql = "update " + userTableName +
				" set sec_locked_date = ? where " + userIdColumnName + " = ?";
		getJdbcTemplate().update(sql, date, username);
	}

	public void unlockUser(String username) {
		String sql = "update " + userTableName +
				" set sec_locked_date = null where " + userIdColumnName + " = ?";
		getJdbcTemplate().update(sql, username);
	}

	public void loginSuccess(UserDetails userDetails) {
		String username = userDetails.getUsername();
		Date date = new Date();

		String sql = "update " + userTableName +
				" set sec_attempts = 0, sec_login_date = ? where " + userIdColumnName + " = ?";
		getJdbcTemplate().update(sql, date, username);
	}

	private List<LoginUserDetails> loadUsersByUsername(String username) {
		return getJdbcTemplate().query(queryUserSql,
			new String[] {username}, (rs, rowNum) -> {
				String cnName = rs.getString("cn_name");
				String password = rs.getString("password");
				boolean admin = rs.getBoolean("admin");
				String email = rs.getString("email");

				Date todayDate = new Date();
				boolean enabled = rs.getBoolean("sec_enabled");
				boolean accountNonExpired = true;
				boolean credentialsNonExpired = true;
				Date accountExpiredDate = rs.getDate("sec_account_expired_date");
				Date credentialsExpiredDate = rs.getDate("sec_credentials_expired_date");
				if (accountExpiredDate != null && todayDate.after(accountExpiredDate)) {
					accountNonExpired = false;
				}
				if (credentialsExpiredDate != null && todayDate.after(credentialsExpiredDate)) {
					credentialsNonExpired = false;
				}

				boolean accountNonLocked = true;
				Date accountLockedDate = rs.getDate("sec_locked_date");

				// 锁定时间到了以后，解锁
				if (accountLockedDate != null && todayDate.before(accountLockedDate)) {
					accountNonLocked = false;
					unlockUser(username);
				}

				boolean credentialsNonUpdatePW = rs.getBoolean("sec_non_update_pw");
				boolean accountConfirm = rs.getBoolean("sec_confirm");

				int loginAttempts = rs.getInt("sec_attempts");

				return new LoginUserDetails(username, password, cnName, admin, email, enabled,
						accountNonExpired, credentialsNonExpired,
						accountNonLocked, credentialsNonUpdatePW,
						accountConfirm, loginAttempts, AuthorityUtils.NO_AUTHORITIES);
			});
	}

	public String getUserTableName() {
		return userTableName;
	}

	public void setUserTableName(String userTableName) {
		this.userTableName = userTableName;
	}

	public String getUserIdColumnName() {
		return userIdColumnName;
	}

	public void setUserIdColumnName(String userIdColumnName) {
		this.userIdColumnName = userIdColumnName;
	}

	public String getCnNameColumnName() {
		return cnNameColumnName;
	}

	public void setCnNameColumnName(String cnNameColumnName) {
		this.cnNameColumnName = cnNameColumnName;
	}
}
