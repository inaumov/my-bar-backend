package mybar.app.bean.users;

import mybar.api.users.WebRole;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RoleAdapter extends XmlAdapter<WebRole, RoleBean> {

    @Override
    public RoleBean unmarshal(WebRole v) throws Exception {
        RoleBean roleBean = new RoleBean();
        roleBean.setWebRole(v);
        return roleBean;
    }

    @Override
    public WebRole marshal(RoleBean v) throws Exception {
        return v.getWebRole();
    }

}