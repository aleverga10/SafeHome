package safehome_se.gui;

import safehome_se.environment.House;

public class ModelDataLoader 
{
    public House house;

    // Singleton pattern
    private static ModelDataLoader instance = null;

    public static ModelDataLoader getInstance() 
    {
        if (instance == null)
            instance = new ModelDataLoader();

        return instance;
    }

}