package com.example.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.entities.Order;
import com.example.entities.User;
import com.example.repository.RepositoryApp;
import com.example.repository.RepositoryUser;


@Controller
@CrossOrigin
public class ControllerApp {
    @Autowired
    RepositoryApp repository;

    @Autowired
    RepositoryUser repositoryUser;

    User userSession = new User();

    @GetMapping("/create")
    public String CreateGet() {
        return "add";
    }

    @PostMapping("/create")
    public String CreatePost(@RequestParam String type_of_equipment, 
                        @RequestParam String model, 
                        @RequestParam String name, 
                        @RequestParam String sur_name, 
                        @RequestParam String last_name,
                        @RequestParam String description_problem,
                        @RequestParam String number_phone,
                        @RequestParam LocalDate date_start,
                        Model model_front) {

        if(userSession.getRoles() != null) {
            Order order = new Order();
            order.setType_of_equipment(type_of_equipment);
            order.setModel(model);
            order.setName(name);
            order.setSur_name(sur_name);
            order.setLast_name(last_name);
            order.setDescription_problem(description_problem);
            order.setNumber_phone(number_phone);
            order.setDate_start(date_start);
            order.setIs_update(false);

            repository.save(order);
            return "add";
        }
        else return "redirect:/registry";

        
    }

    @GetMapping("/findAll")
    public String findOrders(Model model_front) {
        List<Order> orders = repository.findAll();
        if(orders.isEmpty()) model_front.addAttribute("message", "Нет элементов");
        else model_front.addAttribute("orders", orders);
        return "all";
    }

    @GetMapping("/findOne/{id}")
    public String FindOrderById(@PathVariable Integer id,  Model model_front) {
        Optional<Order> order = repository.findById(id);
        if(order.isPresent()) model_front.addAttribute("orders", order);
        return "all";
    }

    // Страницу с обновлением и добавляет туда заявку, который находится по id
    @GetMapping("/update/{id}")
    public String UpdateGet(@PathVariable Integer id, Model model_front) {
        Optional<Order> order = repository.findById(id);
        if(order.isPresent()) model_front.addAttribute("orders", order);
        else model_front.addAttribute("message", "Не найдено");
        return "update";
    }

    // Обновляет заявку, по id, которое там уже доб
    @PostMapping("/update/{id}")
    public String UpdatePost(@PathVariable Integer id,
                            @RequestParam String sur_name,
                            @RequestParam String name,
                            @RequestParam String last_name,
                            @RequestParam String master,
                            @RequestParam String model,
                            @RequestParam String status,
                            @RequestParam LocalDate date_start,
                            @RequestParam LocalDate date_end,
                            @RequestParam String type_of_equipment,
                            @RequestParam String desc_problem,
                            Model model_front) {
        
        Optional<Order> orderOptional = repository.findById(id);
        Order order = new Order();
        if(orderOptional.isPresent()) {
            order = orderOptional.get();
        }
        else model_front.addAttribute("message", "Не найдено");

        order.setSur_name(sur_name);
        order.setName(name);
        order.setLast_name(last_name);
        order.setType_of_equipment(type_of_equipment);
        order.setDescription_problem(desc_problem);
        order.setModel(model);
        order.setMaster(master);
        order.setIs_update(true);
        order.setStatus(status);
        order.setDate_start(date_start);
        order.setDate_end(date_end);

        try{
            repository.save(order);
            model_front.addAttribute("message", "Успешно");
        }
        catch (Exception e) {
            System.out.println(e);
            model_front.addAttribute("message", "Не успешно");
        }
        return "update";
    }

    // Удаление пользователя
    @GetMapping("/delete/{id}")
    public String DeleteGet(@PathVariable Integer id, Model model_front) {
        try{
            repository.deleteById(id);
            model_front.addAttribute("message", "Удалён");
        }
        catch (Exception e) {
            System.out.println(e);
            model_front.addAttribute("message", "Не удалён");
        }
        return "all";
    }

    @GetMapping("/statistic")
    public String StatisticGet(@PathVariable Integer id, Model model_front) {
        List<Order> orders = repository.findAll();
        Map<String, Integer> type_of_equipments = new HashMap<String, Integer>();
        int statistic_count = 0;
        int count = 0;
        int delta_day = 0;
        LocalDate date_now = LocalDate.now();

        // Получение статистики сразу по количеству дней, типам неисправностей и среднему выплнению заявок
        for(Order order : orders) {
            if(type_of_equipments.containsKey(order.getType_of_equipment())) {
                type_of_equipments.put(order.getType_of_equipment(), type_of_equipments.get(order.getType_of_equipment()) + 1);
            }
            else type_of_equipments.put(order.getType_of_equipment(), 1);

            if(order.getStatus() == "Выполнено") statistic_count += 1;

            if(order.getDate_end().isAfter(date_now)) count += 1;
            if(order.getDate_start().getYear() == order.getDate_end().getYear()) {
                delta_day += order.getDate_start().getDayOfYear() - order.getDate_end().getDayOfYear();
            }
            else delta_day += 365 + order.getDate_start().getDayOfYear() - order.getDate_end().getDayOfYear();

            delta_day = delta_day/count;
        }

        model_front.addAttribute("delta_day", delta_day);
        model_front.addAttribute("type_of_equipments", type_of_equipments);
        model_front.addAttribute("statistic_count", statistic_count);

        return "satistic";
    }

    @GetMapping("/registry")
    public String CreateUserGet(@RequestParam String login, 
                        @RequestParam String password, 
                        @RequestParam String role,
                        Model model_front) {

        return "registry";
    }

    @PostMapping("/registry/{id}")
    public String CreateUserPost(@PathVariable Integer id,
                        Model model_front) {

        Optional<User> user = repositoryUser.findById(id);
        if(user.isPresent()) model_front.addAttribute("orders", user);
        else model_front.addAttribute("message", "Не найдено");
        return "registry";
        userSession.setRoles(user.get().getRoles());
    }

    @PostMapping("/login")
    public String Login(@PathVariable Integer id,
                        @RequestParam String login, 
                        @RequestParam String password, 
                        Model model_front) {

        Optional<User> user = repositoryUser.findById(id);
        if(user.isPresent()) {
            if(user.get().getPassword() == password) model_front.addAttribute("orders", user);
            else model_front.addAttribute("message", "Неверный логин или пароль");
        }
        else model_front.addAttribute("message", "Пользователь не найден");
        return "login";
        userSession.setRoles(user.get().getRoles());
    }
     
}
