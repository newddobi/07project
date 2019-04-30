package com.model2.mvc.web.purchase;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.purchase.PurchaseService;

@Controller
public class PurchaseController {
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	public PurchaseController() {
		System.out.println(this.getClass());
	}
	
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	@RequestMapping("/addPurchaseView.do")
	public String addPurchaseView() throws Exception{
		
		System.out.println("/addPurchaseView.do");
		
		return "redirect:/purchase/addPurchaseView.jsp";
	}
	
	@RequestMapping("/addPurchase.do")
	public String addPurchase(@ModelAttribute("purchase") Purchase purchase) throws Exception{
		
		System.out.println("/addPurchase.do");
		
		purchaseService.addPurchase(purchase);
		
		return "redirect:/purchase/addPurchase.jsp";
	}
	
	@RequestMapping("/getPurchase.do")
	public String getPurchase(HttpServletRequest request, Model model) throws Exception{
		
		System.out.println("/getPurchase.do");
		
		Purchase purchase;
		
		if(request.getParameter("prodNo") != null) {
			int prodNo = Integer.parseInt(request.getParameter("prodNo"));
			purchase = purchaseService.getPurchase2(prodNo);
		}else {
			int tranNo = Integer.parseInt(request.getParameter("tranNo"));
			purchase = purchaseService.getPurchase(tranNo);
		}
		
		model.addAttribute("purchase", purchase);
		
		return "forward:/purchase/getPurchase.jsp";
	}
	
	@RequestMapping("/updatePurchaseView.do")
	public String updatePurchaseView(@RequestParam("tranNo") int tranNo, 
																	Model model) throws Exception{
		
		System.out.println("/updatePurchaseView.do");
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		model.addAttribute("purchase", purchase);
		
		return "forward:/purchase/updatePurchaseView.jsp";
	}
	
	@RequestMapping("/updatePurchase.do")
	public String updatePurchase(@ModelAttribute("purchase") Purchase purchase) throws Exception {
		
		System.out.println("/updatePurchase.do");
		
		purchaseService.updatePurchase(purchase);
		
		return "redirect:/getPurchase.do?tranNo="+purchase.getTranNo();
	}
	
	@RequestMapping("/listPurchase.do")
	public String listPurchase(@ModelAttribute("search") Search search,
													Model model, HttpServletRequest request,
													HttpSession session) throws Exception{
		System.out.println("/listPurchase.do");
		
		if(search.getCurrentPage() ==0){
			search.setCurrentPage(1);
		}//언제 currenctPage가 0일까?
		
		if(request.getParameter("currentPage") != null && !request.getParameter("currentPage").equals("")) {
			System.out.println("들어온 currentPage 값 :: "+request.getParameter("currentPage"));
			search.setCurrentPage(Integer.parseInt(request.getParameter("currentPage")));
		}
		
		if(request.getParameter("pageCondition") != null && !request.getParameter("pageCondition").equals("")) {
			pageSize = Integer.parseInt(request.getParameter("pageCondition"));
		}else {
			pageSize = 3;
		}
		
		search.setPageSize(pageSize);
		
		session = request.getSession();
		User user = (User)session.getAttribute("user");
		
		Map<String, Object> map = purchaseService.getPurchaseList(search, user.getUserId());
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		
		System.out.println(resultPage);
		
		// Model 과 View 연결
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "forward:/purchase/listPurchase.jsp";
	}
	
	@RequestMapping("/deletePurchase.do")
	public String deletePurchase(@RequestParam("tranNo") int tranNo) throws Exception{
		
		System.out.println("/deletePurchase.do");
		
		purchaseService.deletePurchase(tranNo);
		
		return "forward:/listPurchase.do";
	}
	
	@RequestMapping("/updateTranCode.do")
	public String updateTranCoed(@RequestParam("prodNo") int prodNo,
															@RequestParam("tranCode") String tranCode) throws Exception{
		
		System.out.println("/updateTranCode.do");
		
		Purchase purchase = new Purchase();
		Product product = new Product();
		
		product.setProdNo(prodNo);
		purchase.setPurchaseProd(product);
		purchase.setTranCode(tranCode);
		
		purchaseService.updateTranCode(purchase);
		
		return "redirect:/getPurchase.do?prodNo="+prodNo;
	}
}//end of class
