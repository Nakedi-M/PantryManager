package com.example.pantrymanager.data;

import java.util.ArrayList;
import java.util.List;

public class RecipeSeeder {

    // Adds the starter recipes the first time the app runs.
    // Must be called on a background thread (Room rule).
    public static void seedIfEmpty(AppDatabase db) {
        RecipeDao dao = db.recipeDao();
        if (dao.countRecipes() > 0) {
            return;   // already seeded, do nothing
        }

        addRecipe(dao, "Tomato Omelette",
                "1. Chop the tomato and onion.\n2. Fry the onion in butter until soft, then add the tomato.\n3. Beat the eggs, pour over, and cook until set.",
                ing("egg", 3, "pcs"), ing("tomato", 1, "pcs"),
                ing("onion", 1, "pcs"), ing("butter", 10, "g"));

        addRecipe(dao, "Cheese Omelette",
                "1. Beat the eggs.\n2. Melt the butter in a pan and add the eggs.\n3. Sprinkle the cheese on top, fold, and serve.",
                ing("egg", 3, "pcs"), ing("cheese", 50, "g"), ing("butter", 10, "g"));

        addRecipe(dao, "Scrambled Eggs",
                "1. Whisk the eggs with the milk.\n2. Melt the butter in a pan on low heat.\n3. Add the eggs and stir gently until just set.",
                ing("egg", 3, "pcs"), ing("milk", 30, "ml"), ing("butter", 10, "g"));

        addRecipe(dao, "Tomato Pasta",
                "1. Boil the pasta until tender.\n2. Fry the onion and garlic, then add chopped tomatoes and simmer 10 minutes.\n3. Mix the sauce through the drained pasta.",
                ing("pasta", 200, "g"), ing("tomato", 3, "pcs"),
                ing("garlic", 2, "pcs"), ing("onion", 1, "pcs"));

        addRecipe(dao, "Mac and Cheese",
                "1. Boil the pasta and drain.\n2. Melt the butter, stir in the flour, then slowly add the milk to make a sauce.\n3. Stir in the cheese until melted and mix with the pasta.",
                ing("pasta", 200, "g"), ing("cheese", 100, "g"), ing("milk", 200, "ml"),
                ing("butter", 20, "g"), ing("flour", 20, "g"));

        addRecipe(dao, "Creamy Chicken Pasta",
                "1. Boil the pasta.\n2. Slice the chicken and fry with the garlic until cooked through.\n3. Add the milk and cheese, simmer until creamy, and toss with the pasta.",
                ing("pasta", 200, "g"), ing("chicken breast", 1, "pcs"), ing("milk", 150, "ml"),
                ing("garlic", 2, "pcs"), ing("cheese", 50, "g"));

        addRecipe(dao, "Garlic Butter Chicken",
                "1. Season the chicken breasts.\n2. Fry in butter for 6 minutes each side.\n3. Add the crushed garlic for the last 2 minutes and spoon the butter over.",
                ing("chicken breast", 2, "pcs"), ing("garlic", 3, "pcs"), ing("butter", 30, "g"));

        addRecipe(dao, "Chicken Fried Rice",
                "1. Cook the rice and let it cool.\n2. Fry the diced chicken, onion and garlic.\n3. Push to one side, scramble the eggs, then mix in the rice.",
                ing("rice", 200, "g"), ing("chicken breast", 1, "pcs"), ing("egg", 2, "pcs"),
                ing("onion", 1, "pcs"), ing("garlic", 2, "pcs"));

        addRecipe(dao, "Egg Fried Rice",
                "1. Cook the rice and let it cool.\n2. Fry the onion and garlic until soft.\n3. Add the beaten eggs, stir, then add the rice and fry until hot.",
                ing("rice", 200, "g"), ing("egg", 2, "pcs"),
                ing("onion", 1, "pcs"), ing("garlic", 1, "pcs"));

        addRecipe(dao, "Tomato Rice",
                "1. Fry the onion and garlic.\n2. Add the chopped tomatoes and cook until soft.\n3. Stir in the rice and enough water to cover, then simmer until the rice is cooked.",
                ing("rice", 200, "g"), ing("tomato", 3, "pcs"),
                ing("onion", 1, "pcs"), ing("garlic", 2, "pcs"));

        addRecipe(dao, "Rice Pudding",
                "1. Put the rice, milk and butter in a pot.\n2. Simmer gently for 30 minutes, stirring often.\n3. Serve when thick and creamy.",
                ing("rice", 100, "g"), ing("milk", 500, "ml"), ing("butter", 10, "g"));

        addRecipe(dao, "Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash with the butter.\n3. Stir in the warm milk until smooth.",
                ing("potato", 4, "pcs"), ing("butter", 30, "g"), ing("milk", 100, "ml"));

        addRecipe(dao, "Spanish Potato Omelette",
                "1. Slice the potatoes and onion and fry gently until soft.\n2. Mix with the beaten eggs.\n3. Cook in the pan until set, flipping once.",
                ing("potato", 3, "pcs"), ing("egg", 4, "pcs"), ing("onion", 1, "pcs"));

        addRecipe(dao, "Cheesy Baked Potatoes",
                "1. Bake whole potatoes at 200 C for about 1 hour.\n2. Cut open and mash the insides with the butter.\n3. Top with the cheese and bake 10 more minutes.",
                ing("potato", 4, "pcs"), ing("cheese", 80, "g"), ing("butter", 20, "g"));

        addRecipe(dao, "Chicken and Potato Bake",
                "1. Cut the potatoes and onion into chunks and place in a dish with the chicken.\n2. Add the crushed garlic and dot with butter.\n3. Bake at 200 C for 45 minutes.",
                ing("chicken breast", 2, "pcs"), ing("potato", 4, "pcs"), ing("onion", 1, "pcs"),
                ing("garlic", 2, "pcs"), ing("butter", 20, "g"));

        addRecipe(dao, "Pancakes",
                "1. Whisk the flour, eggs and milk into a smooth batter.\n2. Melt a little butter in a pan.\n3. Cook spoonfuls for about 2 minutes each side.",
                ing("flour", 150, "g"), ing("egg", 2, "pcs"),
                ing("milk", 250, "ml"), ing("butter", 20, "g"));

        addRecipe(dao, "Cheese Scones",
                "1. Rub the butter into the flour.\n2. Mix in the cheese, egg and milk to form a dough.\n3. Shape, and bake at 200 C for 15 minutes.",
                ing("flour", 250, "g"), ing("butter", 60, "g"), ing("cheese", 100, "g"),
                ing("egg", 1, "pcs"), ing("milk", 100, "ml"));

        addRecipe(dao, "Potato and Onion Soup",
                "1. Fry the chopped onion in butter.\n2. Add the diced potatoes and enough water to cover; simmer until soft.\n3. Blend, stir in the milk, and heat through.",
                ing("potato", 3, "pcs"), ing("onion", 1, "pcs"),
                ing("milk", 200, "ml"), ing("butter", 20, "g"));

        // These two need ingredients that are NOT in the test pantry,
        // so they should never appear in Suggested Recipes during the demo.
        addRecipe(dao, "Cheese Toastie",
                "1. Butter the outside of the bread.\n2. Put the cheese between the slices.\n3. Fry until golden on both sides.",
                ing("bread", 2, "pcs"), ing("cheese", 60, "g"), ing("butter", 10, "g"));

        addRecipe(dao, "Beef Stew",
                "1. Brown the beef in a pot.\n2. Add the chopped onion, carrots and potatoes with enough water to cover.\n3. Simmer for 90 minutes until tender.",
                ing("beef", 300, "g"), ing("potato", 3, "pcs"),
                ing("onion", 1, "pcs"), ing("carrot", 2, "pcs"));
    }

    // Saves one recipe, then saves its ingredients linked by the new recipe ID
    private static void addRecipe(RecipeDao dao, String name, String steps,
                                  RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe();
        recipe.name = name;
        recipe.steps = steps;
        long recipeId = dao.insertRecipe(recipe);

        List<RecipeIngredient> list = new ArrayList<>();
        for (RecipeIngredient ingredient : ingredients) {
            ingredient.recipeId = (int) recipeId;
            list.add(ingredient);
        }
        dao.insertIngredients(list);
    }

    // Small helper so each ingredient fits on one short line above
    private static RecipeIngredient ing(String name, double quantity, String unit) {
        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.name = name;
        ingredient.quantity = quantity;
        ingredient.unit = unit;
        return ingredient;
    }
}