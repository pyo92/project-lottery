/**
 * load event handler
 */
window.addEventListener('load', function () {
    //alert message 표시
    const serverAlert = document.querySelector('.alert.alert-success');
    if (serverAlert != null) {
        alert(serverAlert.textContent.trim());
        serverAlert.remove();
    }
});

//////////////////////////////

const confirmButton = document.getElementById('confirm-btn');

/**
 * 확인 버튼 click event handler
 */
confirmButton.addEventListener('click', function () {
   window.location.href = '/'; //일회용 안내 페이지 이므로, 루트 페이지로 이동
});
