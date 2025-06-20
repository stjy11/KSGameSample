<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/_header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<body>
<header>
	<h1>会員認証</h1>
</header>
    <div class="login-container">
        <div class="content">
            <form id="loginForm" action="${pageContext.request.contextPath}/login" method="post">

                <div class="input-group mb-3">
                    <label class="form-label" for="memberId">会員ID</label>
                    <input type="text" id="memberId" class="form-control" name="memberId" maxlength="6" value="${memberDto.memberId}">
                </div>

                <div class="input-group">
                    <label class="form-label" for="memberPassword">パスワード</label>
                    <input type="password" id="memberPassword" class="form-control" name="memberPassword" maxlength="8" value="${memberDto.memberPassword}">
                </div>

                <button type="submit" class="btn btn-primary login-btn">ログイン</button>
                <div class="login-error-message">
                    <p id="frontendErrorMessage">${errorMessage}</p>
                </div>
                
            </form>
        </div>
    </div>
</body>
</html>