package com.ecom.util;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.UserService;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${aws.s3.bucket.category}")
    private String categoryBucket;

    @Value("${aws.s3.bucket.product}")
    private String productBucket;

    @Value("${aws.s3.bucket.profile}")
    private String profileBucket;

    /* ================= FORGOT PASSWORD MAIL ================= */
    public Boolean sendMail(String url, String recipientEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(mailFrom, "Shopping Cart");
            helper.setTo(recipientEmail);

            String content =
                    "<p>Hello,</p>" +
                    "<p>You requested to reset your password.</p>" +
                    "<p>Click below to change it:</p>" +
                    "<p><a href=\"" + url + "\">Reset Password</a></p>" +
                    "<br><p>If you did not request this, ignore this email.</p>";

            helper.setSubject("Password Reset");
            helper.setText(content, true);

            mailSender.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ================= URL GENERATOR ================= */
    public static String generateUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }

    /* ================= ORDER STATUS MAIL ================= */
    public Boolean sendMailForProductOrder(ProductOrder order, String status) {
        try {
            if (order == null || order.getOrderAddress() == null) return false;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(mailFrom, "Shopping Cart");
            helper.setTo(order.getOrderAddress().getEmail());

            String msg =
                    "<p>Hello " + order.getOrderAddress().getFirstName() + ",</p>" +
                    "<p>Your order status is <b>" + status + "</b>.</p>" +
                    "<p><b>Product:</b> " + order.getProduct().getTitle() + "</p>" +
                    "<p><b>Category:</b> " + order.getProduct().getCategory() + "</p>" +
                    "<p><b>Quantity:</b> " + order.getQuantity() + "</p>" +
                    "<p><b>Price:</b> " + order.getPrice() + "</p>" +
                    "<p><b>Payment:</b> " + order.getPaymentType() + "</p>";

            helper.setSubject("Order Status Update");
            helper.setText(msg, true);

            mailSender.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ================= LOGGED IN USER ================= */
    public UserDtls getLoggedInUserDetails(Principal p) {
        if (p == null) return null;
        return userService.getUserByEmail(p.getName());
    }

    /* ================= IMAGE URL ================= */
    public String getImageUrl(MultipartFile file, Integer bucketType) {

        String bucketName;
        if (bucketType == 1) {
            bucketName = categoryBucket;
        } else if (bucketType == 2) {
            bucketName = productBucket;
        } else {
            bucketName = profileBucket;
        }

        String imageName =
                (file != null && !file.isEmpty())
                        ? file.getOriginalFilename()
                        : "default.jpg";

        return "https://" + bucketName + ".s3.amazonaws.com/" + imageName;
    }
}
