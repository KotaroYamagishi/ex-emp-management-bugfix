package jp.co.sample.emp_management.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.form.InsertEmployeeForm;
import jp.co.sample.emp_management.form.UpdateEmployeeForm;
import jp.co.sample.emp_management.service.EmployeeService;

/**
 * 従業員情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private HttpSession session;

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public UpdateEmployeeForm setUpForm() {
		return new UpdateEmployeeForm();
	}

	@ModelAttribute
	public InsertEmployeeForm setUpForm2() {
		return new InsertEmployeeForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員一覧を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員一覧画面
	 */
	@RequestMapping("/showList")
	public String showList(Model model, String name) {
		session.getAttribute("administratorName");
		if (Objects.isNull(name)) {
			return "employee/list";
		} else {
			List<Employee> employeeList = employeeService.findByName(name);
			if (employeeList.size() == 0) {
				model.addAttribute("errorMessage", "1件もありませんでした");
			}
			List<String> serachList=new ArrayList<String>();
			List<Employee> allEmployee=employeeService.showList();
			for(Employee employee: allEmployee){
				serachList.add(employee.getName());
			}
			model.addAttribute("serachList", serachList);
			model.addAttribute("employeeList", employeeList);
			return "employee/list";
		}
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細画面を出力します.
	 * 
	 * @param id    リクエストパラメータで送られてくる従業員ID
	 * @param model モデル
	 * @return 従業員詳細画面
	 */
	@RequestMapping("/showDetail")
	public String showDetail(String id, Model model) {
		Employee employee = employeeService.showDetail(Integer.parseInt(id));
		model.addAttribute("employee", employee);
		return "employee/detail";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を更新する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細(ここでは扶養人数のみ)を更新します.
	 * 
	 * @param form 従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@RequestMapping("/update")
	public String update(@Validated UpdateEmployeeForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showDetail(form.getId(), model);
		}
		Employee employee = new Employee();
		employee.setId(form.getIntId());
		employee.setDependentsCount(form.getIntDependentsCount());
		employeeService.update(employee);
		return "redirect:/employee/showList";
	}

	@RequestMapping("/insertEmp")
	public String insertEmp(Model model) {
		Map<String, String> genderMap = new HashMap<>();
		genderMap.put("男", "男");
		genderMap.put("女", "女");
		model.addAttribute("genderMap", genderMap);

		return "employee/insertEmployee";
	}

	@RequestMapping("/addEmp")
	public String addEmp(InsertEmployeeForm form, MultipartFile multipartFile) throws Exception {
		Employee employee = new Employee();
		
		try{
			File newFileName = new File(multipartFile.getOriginalFilename());
			System.out.println(newFileName);
			String uploadPath="src/main/resources/static/img/";
			byte[] bytes=multipartFile.getBytes();
			
			BufferedOutputStream stream = new BufferedOutputStream( new FileOutputStream(new File(uploadPath+newFileName)));
			stream.write(bytes);
			stream.close();

			employee.setImage(newFileName.toString());
		}catch(Exception e){
			System.err.println("error");
		}
		
		
		employee.setId(employeeService.findId());
		employee.setName(form.getName());
		employee.setGender(form.getGender());
		try {
			String strDate = form.getYear() + "/" + form.getMonth() + "/" + form.getDay();
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = (Date) sdFormat.parse(strDate);
			employee.setHireDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		employee.setMailAddress(form.getMailAddress());
		employee.setZipCode(form.getZipCode());
		employee.setAddress(form.getAddress1() + form.getAddress2());
		employee.setTelephone(form.getTelephone());
		employee.setSalary(form.getSalary());
		employee.setCharacteristics(form.getCharacteristics());
		employee.setDependentsCount(form.getDependentsCount());

		employeeService.insert(employee);
		session.getAttribute("administratorName");

		return "/employee/list";
	}
}
