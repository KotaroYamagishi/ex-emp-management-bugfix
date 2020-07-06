package jp.co.sample.emp_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sample.emp_management.domain.Administrator;
import jp.co.sample.emp_management.domain.UserDetailsImpl;
import jp.co.sample.emp_management.repository.AdministratorRepository;

/**
 * 管理者情報を操作するサービス.
 * 
 * @author igamasayuki
 *
 */
@Service
@Transactional
public class AdministratorService implements UserDetailsService{
	
	@Autowired
	private AdministratorRepository administratorRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	/**
	 * 管理者情報を登録します.
	 * 
	 * @param administrator　管理者情報
	 */
	public void insert(Administrator administrator) {
		administrator.setPassword(passwordEncoder.encode(administrator.getPassword()));
		administratorRepository.insert(administrator);
	}
	
	/**
	 * ログインをします.
	 * @param mailAddress メールアドレス
	 * @param password パスワード
	 * @return 管理者情報　存在しない場合はnullが返ります
	 */
	public Administrator login(String mailAddress, String passward) {
		Administrator administrator = administratorRepository.findByMailAddressAndPassward(mailAddress, passward);
		return administrator;
	}

	// mailAddressが存在するか確認する
	public Administrator findByMailAddress(String mailAddress) {
		Administrator administrator =administratorRepository.findByMailAddress(mailAddress);
		return administrator;
	}

	@Override
	public UserDetails loadUserByUsername(String mailAddress) throws UsernameNotFoundException {
		if (mailAddress == null || "".equals(mailAddress)) {
            throw new UsernameNotFoundException("Username is empty");
        }

        Administrator administrator = administratorRepository.findByMailAddress(mailAddress);
        if (administrator == null) {
            throw new UsernameNotFoundException("User not found: " + mailAddress);
        }
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl();
        userDetailsImpl.setAdministrator(administrator);

        return userDetailsImpl;
	}

	

}
