package models.responsemodels;

import models.requestmodels.Ingredient;

import java.util.List;

public class IngredientsResponse {
    public String success;
    public List<Ingredient> data;

    public IngredientsResponse() {}

    public IngredientsResponse(String success, List<Ingredient> data) {
        this.success = success;
        this.data = data;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<Ingredient> getData() {
        return data;
    }

    public void setData(List<Ingredient> data) {
        this.data = data;
    }
}
