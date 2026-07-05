package com.telecom.scm.security.service;

import java.util.List;

import com.telecom.scm.security.dto.response.CurrentUserMenuItem;
import com.telecom.scm.security.model.AuthenticatedUser;

public interface CurrentUserMenuService {

    List<CurrentUserMenuItem> listMenus(AuthenticatedUser user);
}
