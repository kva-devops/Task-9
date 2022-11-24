package ru.kata.spring.boot_security.demo.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Set;

@Controller
@AllArgsConstructor
public class IndexController {
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping(value = {"/user", "/admin"})
    public String loadPageUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();
        model.addAttribute("authenticatedUser", authenticatedUser);
        return "user";
    }

    @GetMapping(value = "/admin/")
    public String loadPageAdmin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();
        model.addAttribute("authenticatedUser", authenticatedUser);
        model.addAttribute("users", userService.getAllUsers());
        return "admin-index";
    }

    @GetMapping("/admin/create")
    public String loadPageForCreateUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();
        model.addAttribute("authenticatedUser", authenticatedUser);
        model.addAttribute("roles", roleService.findAllRoles());
        model.addAttribute("user", new User());
        return "create";
    }

    @PostMapping("/admin/create")
    public String createUser(@Valid @ModelAttribute User user,
                             BindingResult bindingResult,
                             HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "error";
        } else {
            String[] authoritiesIds = request.getParameterValues("authoritiesIds");
            Set<Role> roleSet = roleService.findRolesByIds(authoritiesIds);
            user.setRoleSet(roleSet);
            user.setEnabled(true);
            userService.createOrUpdateUser(user);
            return "redirect:/admin/";
        }
    }

    @GetMapping("/admin/delete")
    public String deleteUser(@RequestParam(name = "id") Integer id) {
        userService.deleteUserById(id);
        return "redirect:/admin/";
    }

    @GetMapping("/admin/update")
    public String loadPageForUpdateUser(@RequestParam(name = "id") Integer id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();
        model.addAttribute("authenticatedUser", authenticatedUser);
        model.addAttribute("roles", roleService.findAllRoles());
        model.addAttribute("user", userService.findUserById(id).get());
        return "update";
    }

    @PostMapping("/admin/update")
    public String updateUser(@RequestParam(name = "id") Integer id,
                             @ModelAttribute User user,
                             HttpServletRequest request) {
        String[] authoritiesIds = request.getParameterValues("authoritiesIds");
        if (authoritiesIds != null) {
            Set<Role> roleSet = roleService.findRolesByIds(authoritiesIds);
            user.setRoleSet(roleSet);
        }
        user.setId(id);
        user.setEnabled(true);
        userService.createOrUpdateUser(user);
        return "redirect:/admin/";
    }
}
