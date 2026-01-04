package com.ecom.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			UserDtls userDtls = userService.getUserByEmail(p.getName());
			m.addAttribute("user", userDtls);
			m.addAttribute("countCart", cartService.getCountCart(userDtls.getId()));
		}
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}
	
	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id,
	                                @RequestParam Integer st,
	                                HttpSession session) {

		String status = null;

		for (OrderStatus os : OrderStatus.values()) {
			if (os.getId().equals(st)) {
				status = os.getName();
				break;
			}
		}

		void order = orderService.updateOrderStatus(id, status);

		if (!ObjectUtils.isEmpty(order)) {
			session.setAttribute("succMsg", "Order cancelled successfully");
		} else {
			session.setAttribute("errorMsg", "Order cancel failed");
		}

		return "redirect:/user/my-orders";
	}

	@GetMapping("/my-orders")
	public String myOrders(Principal p, Model model) {

	    UserDtls user = getLoggedInUserDetails(p);

	    List<ProductOrder> orders =
	            orderService.getOrdersByUser(user.getId());

	    model.addAttribute("orders", orders);

	    return "user/my_orders";
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);

		if (!carts.isEmpty()) {
			m.addAttribute("totalOrderPrice",
					carts.get(carts.size() - 1).getTotalOrderPrice());
		}
		return "user/cart";
	}

	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) {
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);

		if (!carts.isEmpty()) {
			Double price = carts.get(carts.size() - 1).getTotalOrderPrice();
			m.addAttribute("orderPrice", price);
			m.addAttribute("totalOrderPrice", price + 350);
		}
		return "user/order";
	}

	@GetMapping("/profile")
	public String profile() {
		return "user/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user,
			@RequestParam MultipartFile img, HttpSession session) {

		UserDtls updated = userService.updateUserProfile(user, img);
		session.setAttribute(
				ObjectUtils.isEmpty(updated) ? "errorMsg" : "succMsg",
				ObjectUtils.isEmpty(updated) ? "Profile not updated" : "Profile updated");

		return "redirect:/user/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword,
			@RequestParam String currentPassword, Principal p, HttpSession session) {

		UserDtls user = getLoggedInUserDetails(p);

		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			session.setAttribute("errorMsg", "Current password incorrect");
			return "redirect:/user/profile";
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userService.updateUser(user);
		session.setAttribute("succMsg", "Password updated");

		return "redirect:/user/profile";
	}

	private UserDtls getLoggedInUserDetails(Principal p) {
		return userService.getUserByEmail(p.getName());
	}
}
