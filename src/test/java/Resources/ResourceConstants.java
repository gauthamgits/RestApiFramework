package Resources;

public enum ResourceConstants {

    AddplaceAPI("maps/api/place/add/json"),
    GetplaceAPI("maps/api/place/get/json"),
    DeleteplaceAPI("maps/api/place/delete/json");
    private String apiname;

    ResourceConstants(String apiname) {
        this.apiname=apiname;

    }
    public String getApiName(){
        return apiname;
    }
}
