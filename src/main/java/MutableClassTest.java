public class MutableClassTest {
    private String name;

    @Tag(method = "Set")
    @Tokens(focalMethod = "setName")
    public String testSetName(String name) {
        this.name = name;
        return name;
    }

    @Tag(method = "Get",scenario = "TwoNames")
    @Tokens(focalMethod = "getName")
    public String testGetName() {
        return name;
    }

    @Tag(scenario = "OneName")
    public void testGet() {
        return;
    }

    @Tag(method = "Push", scenario = "Null")
    public void testPush() {
        return;
    }
}
