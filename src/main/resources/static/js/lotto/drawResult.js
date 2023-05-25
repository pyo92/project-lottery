/**
 * 회차 selector 변경 시, handler
 */
$('#draw-no').on('change', function () {
    //회차 선택 시, 해당 회차로 이동
    $('#draw-result-search-form').submit();
});