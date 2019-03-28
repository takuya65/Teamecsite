package com.internousdev.neptune.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.neptune.dao.CartInfoDAO;
import com.internousdev.neptune.dto.CartInfoDTO;
import com.internousdev.neptune.dto.MCategoryDTO;
import com.opensymphony.xwork2.ActionSupport;

public class AddCartAction extends ActionSupport implements SessionAware{

	private String productName;
	private String productNameKana;
	private String imageFilePath;
	private String imageFileName;
	private String productCount;
	private String releaseCompany;
	private Date releaseDate;
	private String categoryId;
	private List<MCategoryDTO> mCategoryDTOList = new ArrayList<MCategoryDTO>();
	private Map<String, Object> session;

	public String execute() {
		if(session.isEmpty()){
			return "sessionErr";
		}

		String userId = null;
		String tempUserId = null;

		// ログインしているかどうかでuserId/tempUserIdを使用するかを決める
		if(session.containsKey("userId")) {
			userId = String.valueOf(session.get("userId"));
		} else {
			userId = String.valueOf(session.get("tempUserId"));
			tempUserId = String.valueOf(session.get("tempUserId"));
		}

		String result=ERROR;
		int intProductCount = 0;
		CartInfoDAO cartInfoDAO = new CartInfoDAO();

		// 購入個数が1～5個以外ならエラー
		try {
			intProductCount = Integer.parseInt(productCount);
			if(intProductCount > 5 || intProductCount < 1){
				return ERROR;
			}
		}catch (NumberFormatException e) {
			return ERROR;
		}

		// もともとDBにあるカート情報の購入個数と追加する商品の購入個数を足して100より大きい場合はエラー
		int productId = Integer.parseInt(session.get("productId").toString());
		if(cartInfoDAO.getProductCount(productId, userId) + intProductCount  > 100){
			return ERROR;
		}

		int count = 0;

		// 追加する商品のカート情報が既に存在する場合は購入個数を更新する。
		// 存在しない場合は、カート情報を新規作成をする
		if(cartInfoDAO.isExistsCartInfo(userId, productId)){
			count = cartInfoDAO.updateProductCount(userId, productId, intProductCount);
		}else{
			int price = Integer.parseInt(session.get("price").toString());
			count = cartInfoDAO.regist(userId, tempUserId, productId, intProductCount, price);
		}

		if(count > 0) {
			// カート情報を取得
			List<CartInfoDTO> cartInfoDTOList = new ArrayList<CartInfoDTO>();
			cartInfoDTOList = cartInfoDAO.getCartInfoDTOList(userId);
			Iterator<CartInfoDTO> iterator = cartInfoDTOList.iterator();

			if(!(iterator.hasNext())) {
				cartInfoDTOList = null;
			}
			session.put("cartInfoDTOList", cartInfoDTOList);

			// カート合計金額を取得
			try{
				int totalPrice = cartInfoDAO.getTotalPrice(userId);
				session.put("totalPrice", totalPrice);
				if(Integer.parseInt(session.get("totalPrice").toString()) < 0){
					return ERROR;
				}
			}catch(NumberFormatException e){
				return ERROR;
			}
			result=SUCCESS;
		}
		return result;
	}

	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductNameKana() {
		return productNameKana;
	}
	public void setProductNameKana(String productNameKana) {
		this.productNameKana = productNameKana;
	}
	public String getImageFilePath() {
		return imageFilePath;
	}
	public void setImageFilePath(String imageFilePath) {
		this.imageFilePath = imageFilePath;
	}
	public String getImageFileName() {
		return imageFileName;
	}
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}
	public String getProductCount() {
		return productCount;
	}
	public void setProductCount(String productCount) {
		this.productCount = productCount;
	}
	public String getReleaseCompany() {
		return releaseCompany;
	}
	public void setReleaseCompany(String releaseCompany) {
		this.releaseCompany = releaseCompany;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
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