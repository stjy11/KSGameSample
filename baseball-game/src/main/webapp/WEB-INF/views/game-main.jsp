<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/_header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<body>
<header>
	<h1>数当てゲーム</h1>
</header>

<div class="total-point">
	<p>現在のポイント&nbsp;&nbsp;<span id="currentPoints">${gameStat.totalPoints}</span></p>
</div>

    <div class="game-container">
        <div class="game-area">
            <p>隠れ数字: <span class="hidden-number">* * *</span></p>
            <div class="input-section">
                <label for="inputNumber">入力</label>
                <input type="text" id="inputNumber" class="form-control" maxlength="3" pattern="[0-9]{3}" title="0 から 9 までの数字を重複なく 3 桁入力してください。"
                       value=""
                       <c:if test="${gameStat.gameOver}">disabled</c:if>>
                <button id="confirmButton" class="btn btn-primary text-nowrap" <c:if test="${gameStat.gameOver}">disabled</c:if>>確認</button>
            </div>
            <div class="game-system-message"> <p id="message" class="message">${gameStat.message}</p></div>
        </div>

        <div class="record-table">
            <table>
                <thead>
                    <tr>
                        <th>入力回数</th>
                        <th>入力情報</th>
                        <th>判定結果</th>
                    </tr>
                </thead>
                <tbody id="gameRecordsBody">
                    <c:forEach begin="1" end="10" var="i">
                        <c:set var="record" value="${gameStat.gameRecords[i-1]}"/>
                        <tr>
                            <td>${i}回目</td>
                            <td>${record.inputNumber != null ? record.inputNumber : ''}</td>
                            <td>${record.hint != null ? record.hint : ''}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    
    <script>
        $(document).ready(function() {
            function initializeMessageDisplays() {
                
                if ($('#message').text().trim() === '') {
                    $('#message').hide();
                } else {
                    $('#message').show();
                }
                
                $('#hintMessageDisplay').text('').hide(); 
            }

            initializeMessageDisplays(); // 페이지 로드 시 실행

            $('#confirmButton').on('click', function() {
                var inputNumber = $('#inputNumber').val();
                
                $('#message').text('').hide();
                $('#hintMessageDisplay').text('').hide();

                // 클라이언트 측 유효성 검사
                if (inputNumber.length !== 3 || !/^[0-9]{3}$/.test(inputNumber)) {
                    $('#message').text("3 つの数字を入力してください。").show();
                    return;
                }
                if ((new Set(inputNumber.split(''))).size !== 3) {
                     $('#message').text("重複しない数字を入力してください。").show();
                     return;
                }

                $.ajax({
                    url: '<c:url value="/game-guess"/>',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({inputNumber: inputNumber}),
                    success: function(response) {
                        $('#message').text('').hide();
                        $('#hintMessageDisplay').text('').hide();

                       
                        if (response.message && response.message.trim() !== '') {
                            $('#message').text(response.message).show();
                        }

                        if (response.hintMessage && response.hintMessage.trim() !== '') {
                            $('#hintMessageDisplay').text(response.hintMessage).show();
                        }
                        
                        $('#currentPoints').text(response.totalPoints);
                        $('#inputNumber').val(''); // 입력창 초기화

                        // 게임 기록 테이블 업데이트
                        var recordsHtml = '';
                        for (var i = 0; i < 10; i++) {
                            var record = response.gameRecords[i];
                            recordsHtml += '<tr>';
                            recordsHtml += '<td>' + (i + 1) + '回目</td>';
                            recordsHtml += '<td>' + (record && record.inputNumber ? record.inputNumber : '') + '</td>';
                            recordsHtml += '<td>' + (record && record.hint ? record.hint : '') + '</td>';
                            recordsHtml += '</tr>';
                        }
                        $('#gameRecordsBody').html(recordsHtml);

                        // 게임 종료 여부 확인
						if (response.gameOver) {
						            $('#inputNumber').prop('disabled', true);
						            $('#confirmButton').prop('disabled', true);
						            setTimeout(function() {
						                window.open('<c:url value="/game-result"/>', '_blank', 'width=600,height=500,resizable=no,scrollbars=no,status=no,toolbar=no');
						            }, 1000);
						        }
                    },
                    error: function(xhr, status, error) {
                        console.error("AJAX Error: ", status, error);
                        $('#message').text("ゲーム処理中にエラーが発生しました。").show();
                        $('#hintMessageDisplay').text('').hide(); 
                        $('#inputNumber').prop('disabled', true);
                        $('#confirmButton').prop('disabled', true);
                    }
                });
            });

           
            $('#inputNumber').on('keypress', function(e) {
                if (e.which === 13) {
                    e.preventDefault();
                    $('#confirmButton').click();
                }
            });
        });
    </script>
</body>
</html>
