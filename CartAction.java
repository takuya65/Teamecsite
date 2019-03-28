package com.internousdev.neptune.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.neptune.dao.CartInfoDAO;
import com.internousdev.neptune.dao.MCategoryDAO;
import com.internousdev.neptune.dto.CartInfoDTO;
import com.internousdev.neptune.dto.MCategoryDTO;
import com.opensymphony.xwork2.ActionSupport;

public class CartAction extends ActionSupport implements SessionAware{
	private String categoryId;
	private List<MCategoryDTO> mCategoryDTOList = new ArrayList<MCategoryDTO>();
	private Map<String, Object> session;

	public String execute() {
		if(session.isEmpty()){
			return "sessionErr";
		}

		String userId = null;
		CartInfoDAO cartInfoDAO = new CartInfoDAO();
		List<CartInfoDTO> cartInfoDTOList = new ArrayList<CartInfoDTO>();

		if(session.containsKey("userId")) {
			userId = String.valueOf(session.get("userId"));
		}else{
			userId = String.valueOf(session.get("tempUserId"));
		}

		cartInfoDTOList = cartInfoDAO.getCartInfoDTOList(userId);
		Iterator<CartInfoDTO> iterator = cartInfoDTOList.iterator();
		if(!(iterator.hasNext())) {
			cartInfoDTOList = null;
		}

		session.put("cartInfoDTOList", cartInfoDTOList);
		int totalPrice = cartInfoDAO.getTotalPrice(userId);
		session.put("totalPrice", totalPrice);
		if(!session.containsKey("mCategoryList")) {
			MCategoryDAO mCategoryDAO = new MCategoryDAO();
			mCategoryDTOList = mCategoryDAO.getMCategoryList();
			session.put("mCategoryDTOList", mCategoryDTOList);
		}
		return SUCCESS;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public List<MCategoryDTO> getMCategoryDTOList() {
		return mCategoryDTOList;
	}
	public void setMCategoryDTOList(List<MCategoryDTO> mCategoryDTOList) {
		this.mCategoryDTOList = mCategoryDTOList;
	}

	public Map<String, Object> getSession() {
		return session;
	}
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}