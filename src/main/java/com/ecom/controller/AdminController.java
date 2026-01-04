package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* ---------------- COMMON DATA ---------------- */
    @ModelAttribute
    public void commonUser(Principal p, Model m) {
        if (p != null) {
            UserDtls user = userService.getUserByEmail(p.getName());
            m.addAttribute("user", user);
            m.addAttribute("countCart", cartService.getCountCart(user.getId()));
        }
    }

    /* ---------------- DASHBOARD ---------------- */
    @GetMapping("/")
    public String index() {
        return "admin/index";
    }

    /* ---------------- USERS / ADMINS ---------------- */
    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "1") Integer type, Model m) {

        List<UserDtls> users =
                (type == 1)
                        ? userService.getUsers("ROLE_USER")
                        : userService.getUsers("ROLE_ADMIN");

        m.addAttribute("users", users != null ? users : List.of());
        m.addAttribute("userType", type);
        return "admin/users";
    }

    @GetMapping("/updateSts")
    public String updateStatus(@RequestParam Boolean status,
                               @RequestParam Integer id,
                               @RequestParam Integer type,
                               HttpSession session) {

        Boolean updated = userService.updateAccountStatus(id, status);

        if (updated) {
            session.setAttribute("succMsg", "Status updated successfully");
        } else {
            session.setAttribute("errorMsg", "User not found");
        }

        return "redirect:/admin/users?type=" + type;
    }

    /* ---------------- ORDERS ---------------- */
    @GetMapping("/orders")
    public String orders(@RequestParam(defaultValue = "0") Integer pageNo,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         Model m) {

        Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);

        m.addAttribute("orders", page.getContent());
        m.addAttribute("srch", false);
        m.addAttribute("pageNo", pageNo);
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

        return "admin/orders";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id,
                                    @RequestParam String status,
                                    HttpSession session) {

        // Call service with String status
        ProductOrder order = orderService.updateOrderStatus(id, status);

        if (order != null) {
            session.setAttribute("succMsg", "Order status updated");
        } else {
            session.setAttribute("errorMsg", "Order update failed");
        }

        return "redirect:/admin/orders";
    }



    /* ---------------- ADMIN PROFILE ---------------- */
    @GetMapping("/profile")
    public String profile() {
        return "admin/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user,
                                @RequestParam MultipartFile img,
                                HttpSession session) {

        UserDtls updated = userService.updateUserProfile(user, img);

        if (!ObjectUtils.isEmpty(updated)) {
            session.setAttribute("succMsg", "Profile updated");
        } else {
            session.setAttribute("errorMsg", "Profile update failed");
        }

        return "redirect:/admin/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 Principal p,
                                 HttpSession session) {

        UserDtls user = commonUtil.getLoggedInUserDetails(p);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            session.setAttribute("errorMsg", "Current password is wrong");
            return "redirect:/admin/profile";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);
        session.setAttribute("succMsg", "Password updated");

        return "redirect:/admin/profile";
    }

    /* ---------------- ADD ADMIN ---------------- */
    @GetMapping("/add-admin")
    public String addAdminPage() {
        return "admin/add-admin";  // matches your resources folder
    }

    @PostMapping("/save-admin")
    public String saveAdmin(@ModelAttribute UserDtls user,
                            @RequestParam String cpassword,
                            HttpSession session) {

        if (!user.getPassword().equals(cpassword)) {
            session.setAttribute("errorMsg", "Passwords do not match");
            return "redirect:/admin/add-admin";
        }

        if (userService.existsEmail(user.getEmail())) {
            session.setAttribute("errorMsg", "Email already exists");
            return "redirect:/admin/add-admin";
        }

        userService.saveAdmin(user);
        session.setAttribute("succMsg", "Admin added successfully");

        return "redirect:/admin/add-admin";
    }
}
