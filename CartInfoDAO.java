package com.internousdev.neptune.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.internousdev.neptune.dto.CartInfoDTO;
import com.internousdev.neptune.util.DBConnector;

public class CartInfoDAO {

	//cart_infoテーブルとproduct_infoテーブルとの接続＋結合＋情報の読み込み＋名称変更メソッド
	public List<CartInfoDTO> getCartInfoDTOList(String userId){
		DBConnector dbConnector = new DBConnector();
		Connection connection = dbConnector.getConnection();
		List<CartInfoDTO> cartInfoDTOList = new ArrayList<CartInfoDTO>();

		String sql = "select"
				+ " ci.id as id,"
				+ " ci.user_id as user_id,"
				+ " ci.temp_user_id as temp_user_id,"
				+ " ci.product_id as product_id,"
				+ " ci.product_count as product_count,"
				+ " ci.price as price,"
				+ " pi.product_name as product_name,"
				+ " pi.product_name_kana as product_name_kana,"
				+ " pi.image_file_path as image_file_path,"
				+ " pi.image_file_name as image_file_name,"
				+ " pi.release_date as release_date,"
				+ " pi.release_company as release_company,"
				+ " pi.status as status,"
				+ " (ci.product_count * ci.price) as subtotal,"
				+ " ci.regist_date as regist_date,"
				+ " ci.update_date as update_date"
				+ " FROM cart_info as ci"
				+ " LEFT JOIN product_info as pi"
				+ " ON ci.product_id = pi.product_id"
				+ " WHERE ci.user_id = ?"
				+ " order by update_date desc, regist_date desc";

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1,userId);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				CartInfoDTO cartInfoDTO = new CartInfoDTO();
				cartInfoDTO.setId(resultSet.getInt("id"));
				cartInfoDTO.setUserId(resultSet.getString("user_id"));
				cartInfoDTO.setTempUserId(resultSet.getString("temp_user_id"));
				cartInfoDTO.setProductId(resultSet.getInt("product_id"));
				cartInfoDTO.setProductCount(resultSet.getInt("product_count"));
				cartInfoDTO.setPrice(resultSet.getInt("price"));
				cartInfoDTO.setRegistDate(resultSet.getDate("regist_date"));
				cartInfoDTO.setUpdateDate(resultSet.getDate("update_date"));
				cartInfoDTO.setProductName(resultSet.getString("product_name"));
				cartInfoDTO.setProductNameKana(resultSet.getString("product_name_kana"));
				cartInfoDTO.setImageFilePath(resultSet.getString("image_file_path"));
				cartInfoDTO.setImageFileName(resultSet.getString("image_file_name"));
				cartInfoDTO.setReleaseDate(resultSet.getDate("release_date"));
				cartInfoDTO.setReleaseCompany(resultSet.getString("release_company"));
				cartInfoDTO.setStatus(resultSet.getString("status"));
				cartInfoDTO.setSubtotal(resultSet.getInt("subtotal"));
				cartInfoDTOList.add(cartInfoDTO);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				connection.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return cartInfoDTOList;
	}

	//cart_infoテーブルとproduct_infoテーブルとの接続＋結合＋情報の読み込み＋名称変更メソッド
		public int getProductCount(int productId, String userId){
			DBConnector dbConnector = new DBConnector();
			Connection connection = dbConnector.getConnection();
			int result = 0;

			String sql = "select"
					+ " product_count"
					+ " FROM cart_info"
					+ " WHERE product_id = ? AND user_id = ?";

			try {
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1,productId);
				preparedStatement.setString(2,userId);
				ResultSet resultSet = preparedStatement.executeQuery();
				if(resultSet.next()) {
					result = resultSet.getInt("product_count");
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}finally {
				try {
					connection.close();
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
			return result;
		}


	//カート内の同名商品の合計金額のメソッド
	public int getTotalPrice(String userId) {
		int totalPrice = 0;
		DBConnector dbConnector = new DBConnector();
		Connection connection = dbConnector.getConnection();

		String sql = "select sum(product_count * price) as total_price from cart_info"
				+ " where user_id=? group by user_id";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				totalPrice = resultSet.getInt("total_price");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return totalPrice;
	}

	//カート内の情報をcart_infoに書き込む(ユーザーid,ゲストid,製品id,個数,値段,登録日、更新日)メソッド
	public int regist(String userId,String tempUserId,int productId,int productCount,int price) {
		DBConnector dbConnector = new DBConnector();
		Connection connection = dbConnector.getConnection();
		int count = 0;
		String sql = "insert into cart_info(user_id,temp_user_id,product_id,product_count,price,regist_date,update_date)"
				+ " values (?,?,?,?,?,now(),now())";

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, tempUserId);
			preparedStatement.setInt(3, productId);
			preparedStatement.setInt(4, productCount);
			preparedStatement.setInt(5, price);
			count = preparedStatement.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				connection.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	//カートに存在する商品の購入履歴情報を更新メソッド
	public int updateProductCount(String userId, int productId, int productCount){
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "UPDATE cart_info SET product_count=(product_count + ?), update_date = now() "
				+ " WHERE user_id=? AND product_id=?";

		int result = 0;

		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1,productCount);
			ps.setString(2,userId);
			ps.setInt(3,productId);
			result = ps.executeUpdate();

		}catch(SQLException e){
			e.printStackTrace();
		} finally {
			try{
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return result;
	}

	//userIdと製品idの消去メソッド
	public int delete(String productId,String userId) {
		DBConnector dbConnector = new DBConnector();
		Connection connection = dbConnector.getConnection();
		int count = 0;
			String sql = "delete from cart_info WHERE product_id = ? AND user_id = ?";

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, productId);
			preparedStatement.setString(2, userId);

			count = preparedStatement.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				connection.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	//ユーザーIDの消去メソッド　
	public int deleteAll(String userId) {
		DBConnector dbConnector = new DBConnector();
		Connection connection = dbConnector.getConnection();
		int count = 0;
		String sql = "delete from cart_info where user_id = ?";

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			count = preparedStatement.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				connection.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	//過去の購入履歴確認メソッド
	public boolean isExistsCartInfo(String userId,int productId) {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		String sql = "SELECT COUNT(id) AS COUNT FROM cart_info"
				+ " WHERE user_id = ? AND product_id = ?";

		boolean result = false;

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, productId);
			ResultSet rs = ps.executeQuery();

			if(rs.next()) {
				if(rs.getInt("COUNT") > 0) {
					result = true;
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	//ゲストIDをcart_infoテーブルのメインIDに上書きメソッド
	public int linkToUserId(String tempUserId,String userId,int productId) {
		DBConnector dbConnector = new DBConnector();
		Connection connection = dbConnector.getConnection();
		int count = 0;
		String sql = "UPDATE cart_info SET user_id = ?,temp_user_id = null, update_date = now()"
				+ " WHERE temp_user_id = ? AND product_id = ?";

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			preparedStatement.setString(2, tempUserId);
			preparedStatement.setInt(3, productId);
			count = preparedStatement.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				connection.close();
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

}