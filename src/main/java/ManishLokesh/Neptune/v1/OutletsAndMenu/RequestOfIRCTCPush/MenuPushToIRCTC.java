package ManishLokesh.Neptune.v1.OutletsAndMenu.RequestOfIRCTCPush;import java.util.List;public class MenuPushToIRCTC {    private List<MenuPushRequestBody> menuItems;    @Override    public String toString() {        return "MenuPushToIRCTC{" +                "menuItems=" + menuItems +                '}';    }    public MenuPushToIRCTC(List<MenuPushRequestBody> menuItems) {        this.menuItems = menuItems;    }    public List<MenuPushRequestBody> getMenuItems() {        return menuItems;    }    public void setMenuItems(List<MenuPushRequestBody> menuItems) {        this.menuItems = menuItems;    }}