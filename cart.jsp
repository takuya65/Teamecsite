<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<jsp:include page="header.jsp" >
  <jsp:param name="title" value="カート画面" />
</jsp:include>

      <div id="container">
      	<script>
			function checkValue(check){
					var checkList = document.getElementsByClassName("checkList");
					var checkFlag = 0;
					for (  var i = 0;  i < checkList.length;  i++  ) {
						if(checkFlag == 0){
							if(checkList[i].checked) {
								checkFlag = 1;
								break;
							}
						}
					}
				if (checkFlag == 1) {
			    	document.getElementById('delete_btn').disabled="";
				} else {
					document.getElementById('delete_btn').disabled="true";
				}
			}
		</script>
      	<div id="page-title">
			<h1>Cart</h1>
			<p>カート情報</p>
		</div>

<s:if test="#session.cartInfoDTOList.size()>0">
	<s:form action="SettlementConfirmAction">
	<div id="nep-parallel-table">
		<table>
			<tr>
				<th><s:label value="#"/></th>
				<th><s:label value="商品名"/></th>
				<th><s:label value="商品名ふりがな"/></th>
				<th><s:label value="商品画像"/></th>
				<th><s:label value="値段"/></th>
				<th><s:label value="発売会社名"/></th>
				<th><s:label value="発売年月日"/></th>
				<th><s:label value="購入個数"/></th>
				<th><s:label value="合計金額"/></th>
			</tr>
		<s:iterator value="#session.cartInfoDTOList">
			<tr>
				<td class="center"><span><s:checkbox name="checkList" class="checkList" value="checked" fieldValue="%{productId}"  onchange="checkValue(this)"/></span></td>
				<s:hidden name="productId" value="%{productId}"/>
				<td><span><s:property value="productName"/></span></td>
				<td><span><s:property value="productNameKana"/></span></td>
				<td><div id="nep-parallel-table-img"><img src='<s:property value="imageFilePath"/>/<s:property value="imageFileName"/>'/></div></td>
				<td><span><s:property value="price"/>円</span></td>
				<td><span><s:property value="releaseCompany"/></span></td>
				<td><span><s:property value="releaseDate"/></span></td>
				<td><span><s:property value="productCount"/></span></td>
				<td><span><s:property value="subtotal"/>円</span></td>
			</tr>
		</s:iterator>
		</table>
	</div>
	<div id="top">
		<h2><s:label value="カート合計金額 :"/><s:property value="#session.totalPrice"/>円</h2>
	</div>
	
	<div id="submit">
		<div id="submit-btn">
		  <s:submit value="決済" class="submit_btn"/>
		</div>
	</div>
	<div id="submit">
		<div id="submit-btn">
			<s:submit value="削除" id="delete_btn" class="submit_btn" onclick="this.form.action='DeleteCartAction';" disabled="true"/>
		</div>
	</div>
	</s:form>
</s:if>
<s:else>
<div id="other">
	<div class="other-box">   
		<p>カート情報がありません。</p>
	</div>
</div>
</s:else>
　</div>
</body>
</html>