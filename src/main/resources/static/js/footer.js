/**
 * DOMContentLoaded event handler
 */
document.addEventListener('DOMContentLoaded', function() {
    // 스크롤 맨 위로 버튼
    const scrollTopButton = document.getElementById('scroll-top');
    scrollTopButton.addEventListener('click', function () {
        window.scrollTo(0, 0);
    });

    // 스크롤 맨 아래로 버튼
    const scrollBottomButton = document.getElementById('scroll-bottom');
    scrollBottomButton.addEventListener('click', function () {
        window.scrollTo(0, document.body.scrollHeight);
    });
});