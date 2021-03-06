package pl.rkulig.pizza.controller;

import java.security.Principal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import pl.rkulig.pizza.data.IngredientRepository;
import pl.rkulig.pizza.data.PizzaRepository;
import pl.rkulig.pizza.data.UserRepository;
import pl.rkulig.pizza.model.Ingredient;
import pl.rkulig.pizza.model.Ingredient.Type;
import pl.rkulig.pizza.model.Order;
import pl.rkulig.pizza.model.Pizza;
import pl.rkulig.pizza.model.User;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/design")
@SessionAttributes("order")
@Slf4j
public class DesignPizzaController {


    private final IngredientRepository ingredientRepo;

    private PizzaRepository pizzaRepo;

    private UserRepository userRepo;

    @Autowired
    public DesignPizzaController(IngredientRepository ingredientRepo, PizzaRepository pizzaRepo, UserRepository userRepo) {
        this.ingredientRepo = ingredientRepo;
        this.pizzaRepo = pizzaRepo;
        this.userRepo=userRepo;
    }

    @ModelAttribute(name = "order")
    public Order order() {
        return new Order();
    }

    @ModelAttribute(name = "design")
    public Pizza design(){
        return new Pizza();
    }

    //
//    @ModelAttribute
//    public void addIngredientToModel(Model model) {
//        List<Ingredient> ingredients = Arrays.asList(
//                new Ingredient("CIEN", "cienkie", Type.DOUGH),
//                new Ingredient("GRUB", "grube", Type.DOUGH),
//                new Ingredient("KURC", "kurczak", Type.PROTEIN),
//                new Ingredient("SALA", "salami", Type.PROTEIN),
//                new Ingredient("PAPR", "papryka", Type.VEGGIES),
//                new Ingredient("PIEC", "pieczarki", Type.VEGGIES),
//                new Ingredient("KETC", "ketchup", Type.SAUCE),
//                new Ingredient("CZOS", "czosnkowy", Type.SAUCE)
//        );
//
//        Type[] types = Ingredient.Type.values();
//        for (Type type : types) {
//            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients,type));
//        }
//    }
    @GetMapping
    public String showDesignform(Model model, Principal principal) {
        log.info("   --- Designing pizza");
       List<Ingredient> ingredients = new ArrayList<>();
       ingredientRepo.findAll().forEach(i->ingredients.add(i));

       Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }

        String username = principal.getName();
        User user = userRepo.findByUsername(username);
        model.addAttribute("user", user);
        return "design";
    }

    @PostMapping
    public String processDesign(@Valid Pizza pizza, Errors errors, @ModelAttribute Order order) {
        log.info("   --- Saving pizza");
        if (errors.hasErrors()) {
            return "design";
        }
        Pizza saved = pizzaRepo.save(pizza);
        order.addDesign(saved);
        return "redirect:/orders/current";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients.stream().filter(x -> x.getType().equals(type)).collect(Collectors.toList());
    }
}
