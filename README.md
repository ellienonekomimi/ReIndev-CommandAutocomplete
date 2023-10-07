# ReIndev-CommandAutocomplete
This is a ReIndev FoxLoader mod that replaces the native command helper gui with a simple autocompletion gui similiar to modern Minecraft.

Use the up and down arrows to navigate suggested commands/arguments, and then tab to autocomplete it.

# Adding Your Own Autocomplete
By default, all registered commands have autocomplete. As in, typing /commandNameHere will suggest the command's name.
However, adding custom argument autocomplete for something like /give [item name] would require some work on your end.

So here's a simple tutorial on how to do just that:

Step 1: Making a command autocomplete class

All your autocomplete class needs is a method like this: 
```public List<String> getCommandSuggestions(GuiChat gui, int commandArgIndex)```

The gui and commandArgIndex arguments are passed to provide some sort of context for what's currently typed.
If commandArgIndex is 1, that means the player is currently on the first argument of the command.

The /weather command works by checking if commandArgIndex is equal to 1. If so, it returns a list of suggestions that begin with what is currently typed.
Example 1: "/weather " yields a list of all weather forecasts (clear, rain, thunder)
Example 2: "/weather r" yields a list of only rain as it's the only forecast which begins with "r"
Example 3: "/give diamond" yields a list of all items whose names begin with "diamond", so "diamond_sword", "diamond_axe", and so on.

Step 2: Implement your autocomplete class

FoxLoader's ModContainer class has a static method called getModContainer(String modId)
We can use this to get the autocomplete mod's mod container, which contains the client mod.
From there, we can use reflection to invoke the "addAutocomplete" method with a command name and autocomplete class instance.
Example initiailzation:

```
  public void initAutocomplete() {
        ModContainer autocompleteModContainer = ModLoader.getModContainer("jelliedautocomplete");

        // Autocomplete mod is not installed, so don't do anything.
        if (autocompleteModContainer == null) {
            return;
        }

        Object clientMod = autocompleteModContainer.getClientMod(); // Get the client mod

        try {
            // Get the addAutocomplete method
            Method addAutocompleteMethod =  clientMod.getClass().getMethod("addAutocomplete", String.class, Object.class);

            // Invoke the addAutocomplete method with our command name and an autocomplete object
            addAutocompleteMethod.invoke(clientMod, "/exampleCommand", new ExampleCommandAutocomplete());
            addAutocompleteMethod.invoke(clientMod, "/anotherExampleMethod", new AnotherExampleCommandAutocomplete());

            System.out.println("Successfully initialized command autocompletion.");
        }
        catch (Exception e) {
            System.out.println("Could not initialize command autocompletion:");
            e.printStackTrace();
        }
  }
```

If you have any questions or run into any problems, feel free to contact me on discord (@jelliedbanana)
You can also open a new issue if there's a problem with the mod itself.
