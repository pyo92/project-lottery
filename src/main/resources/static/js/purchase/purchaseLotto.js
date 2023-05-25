let deposit = 0;
let purchasableCount = 0;

/**
 * load event handler
 */
window.addEventListener('load', function () {
    //예치금과 구매가능 게임 수를 세팅 (변조 방지용 변수)
    deposit = parseInt($('#deposit').text().replace(/[^0-9]/g, ''));
    purchasableCount = parseInt($('#purchasable-count').text());

    //alert message 표시
    const serverAlert = document.querySelector('.alert.alert-danger');
    if (serverAlert != null) {
        alert(serverAlert.textContent.trim());
        serverAlert.remove();
    }
});

//////////////////////////////

const refreshSpinner = document.querySelector('#refresh-spinner');
const refreshButton = document.querySelector('#refresh-btn');

/**
 * 예치금, 구매가능 금액 업데이트 버튼 처리
 */
function refresh () {
    keyDownPrevent = true;
    refreshSpinner.style.display = 'flex';

    //예치금, 구매가능 정보를 새로고침 해야한다.
    $.ajax({
        type: 'POST',
        url: '/purchase/dh/refresh',
        success: function (response) {
            $('#deposit').text(response.deposit.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','));
            $('#purchasable-count').text(response.purchasableCount);

            //변조방지용 변수를 리프레시 한다.
            deposit = parseInt($('#deposit').text().replace(/[^0-9]/g, ''));
            purchasableCount = parseInt($('#purchasable-count').text());
        },
        error: function (xhr) {
            window.location.href = xhr.getResponseHeader('Location');
        },
        complete: function () {
            keyDownPrevent = false;
            refreshSpinner.style.display = 'none';
        }
    });
}

refreshButton.addEventListener('click', refresh);

//////////////////////////////

const depositModal = new bootstrap.Modal(document.querySelector('#deposit-modal'));
const depositSpinner = document.querySelector('#deposit-spinner');
const depositButton = document.querySelector('#deposit-btn');

/**
 * 예치금 입금 신청 버튼 click event handler
 */
depositButton.addEventListener('click', function () {
    keyDownPrevent = true;
    depositSpinner.style.display = 'flex';

    //selenium 으로 입금신청을하고, 계좌와 입금액을 가져온다. (5000원으로 고정)
    $.ajax({
        type: 'POST',
        url: '/purchase/dh/deposit',
        success: function (response) {
            $('.account-name').text(response.accountName);
            $('.account-number').text(response.accountNumber);
            $('.deposit-amount').text(response.depositAmount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',') + '원');

            //modal 표시
            depositModal.show();
        },
        error: function (xhr) {
            window.location.href = xhr.getResponseHeader('Location');
        },
        complete: function () {
            keyDownPrevent = false;
            depositSpinner.style.display = 'none';
        }
    });
});

//////////////////////////////

const depositConfirmButton = document.querySelector('#deposit-confirm-btn');

/**
 * 예치금 입금 신청 확인 modal 확인 버튼 click event handler
 */
depositConfirmButton.addEventListener('click', function () {
    //예치금, 구매가능 정보 갱신
    refresh();

    //modal 닫기
    depositModal.hide();
});

//////////////////////////////

const checkboxes = document.querySelectorAll('input[type="checkbox"][name^="number"]');
const initCheckbox = document.querySelector('input[type="checkbox"][name^="init"]');
const autoCheckbox = document.querySelector('input[type="checkbox"][name^="auto"]');
const addButton = document.querySelector('#add-btn');
const purchaseButton = document.querySelector('#purchase-btn');
let gameCount = 0;
let selectedNumbers = [];

/**
 * 로또 번호 OMR 체크 선택 처리
 */
function handleCheckboxChange() {
    selectedNumbers = [];
    checkboxes.forEach((checkbox) => {
        if (checkbox.checked) {
            selectedNumbers.push(checkbox.value);
        }
    });

    if (selectedNumbers.length > 6) {
        this.checked = false;
        selectedNumbers.pop();
        alert('숫자는 6개까지만 선택할 수 있습니다.');

    } else if (selectedNumbers.length === 6) {
        autoCheckbox.checked = false;
    }
}

checkboxes.forEach((checkbox) => {
    checkbox.addEventListener('change', handleCheckboxChange);
});

/**
 * 로또 번호 OMR 초기화 버튼 처리
 */
function handleInitCheckBoxChange() {
    selectedNumbers = [];
    checkboxes.forEach((checkbox) => {
        checkbox.checked = false;
    });

    autoCheckbox.checked = false;
    this.checked = false;
}

initCheckbox.addEventListener('change', handleInitCheckBoxChange);

/**
 * 로또 번호 OMR 자동 선택 버튼 처리
 */
function handleAutoCheckBoxChange() {
    if (selectedNumbers.length === 6) {
        alert('이미 6개의 숫자를 선택하셨습니다.');
        autoCheckbox.checked = false;
    }
}

autoCheckbox.addEventListener('change', handleAutoCheckBoxChange);

/**
 * 로또 번호 선택 후, 게임 추가 버튼 처리
 */
function handleAddButtonClick() {
    let isError = false;
    selectedNumbers.forEach(number => {
        isError |= (isNaN(number) || number < 1 || number > 45);
    });

    if (isError) {
        alert('비정상적인 접근이 감지되어 페이지를 새로고침합니다.');
        location.reload();
        return;
    }

    if (selectedNumbers.length < 6 && !autoCheckbox.checked) {
        alert('숫자 또는 자동을 선택해주세요.');
        return;
    }

    if (gameCount === purchasableCount) { //변조방지 변수로 비교
        alert('더 이상 추가할 수 없습니다.');
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/purchase/dh/L645/game',
        data: {
            number1: selectedNumbers[0],
            number2: selectedNumbers[1],
            number3: selectedNumbers[2],
            number4: selectedNumbers[3],
            number5: selectedNumbers[4],
            number6: selectedNumbers[5]
        },
        success: function (response) {
            const gameNumbers = document.querySelectorAll('.game-numbers');

            for (let i = 0 ; i < 5; i++) {
                const numbers = gameNumbers[i];
                const deleteButton = numbers.querySelector('.btn');
                if (deleteButton.disabled) {
                    const balls = numbers.querySelectorAll('.ball');
                    balls[0].textContent = response.number1;
                    balls[0].classList.add(getNumberClass(balls[0].textContent));
                    balls[1].textContent = response.number2;
                    balls[1].classList.add(getNumberClass(balls[1].textContent));
                    balls[2].textContent = response.number3;
                    balls[2].classList.add(getNumberClass(balls[2].textContent));
                    balls[3].textContent = response.number4;
                    balls[3].classList.add(getNumberClass(balls[3].textContent));
                    balls[4].textContent = response.number5;
                    balls[4].classList.add(getNumberClass(balls[4].textContent));
                    balls[5].textContent = response.number6;
                    balls[5].classList.add(getNumberClass(balls[5].textContent));
                    deleteButton.disabled = '';
                    gameCount++;

                    break;
                }
            }
        }
    });
}

/**
 * 게임 추가 시, ball class 계산 및 부여
 * @param number 선택 번호
 * @returns {string} ball class
 */
function getNumberClass(number) {
    if (number >= 1 && number <= 10) {
        return 'n0';
    } else if (number >= 11 && number <= 20) {
        return 'n1';
    } else if (number >= 21 && number <= 30) {
        return 'n2';
    } else if (number >= 31 && number <= 40) {
        return 'n3';
    } else if (number >= 41 && number <= 45) {
        return 'n4';
    } else {
        return '';
    }
}

addButton.addEventListener('click', handleAddButtonClick);

/**
 * 게임 추가 후, 구매 버튼 처리
 */
function handlePurchaseButtonClick() {
    if (gameCount < 1) {
        alert('게임이 존재하지 않습니다.');
        return;
    }

    if (deposit < gameCount * 1000) {
        alert('예치금 잔액이 부족합니다.');
        return;
    }

    const games = $('.game-grid').children('.game-numbers');

    //부적절한 값이 없는지 검증
    let isError = false;

    games.each(function () {
        if (!$(this).children('.btn').prop('disabled')) { //숫자가 담겨있는 행만

            // 현재 .game-numbers 요소의 자식 요소인 .ball을 선택합니다.
            const numbers = $(this).children('.ball');

            // 현재 .game-numbers 요소에 속한 모든 .ball 요소의 값을 sets 배열에 저장합니다.
            numbers.each(function () {
                const number = parseInt($(this).text());
                isError |= (isNaN(number) || number < 1 || number > 45);
            });
        }
    });

    if (isError) {
        alert('비정상적인 접근이 감지되어 페이지를 새로고침합니다.');
        location.reload();
        return;
    }

    //회차 타이머에서 draw no 가져와서 세팅
    $('#draw-no').val(drawNo);


    // 각 .game-numbers 요소에 대해 반복합니다.
    games.each(function (idx) {
        if (!$(this).children('.btn').prop('disabled')) { //숫자가 담겨있는 행만


            // 현재 .game-numbers 요소의 자식 요소인 .ball을 선택합니다.
            const numbers = $(this).children('.ball');
            let game = "";

            // 현재 .game-numbers 요소에 속한 모든 .ball 요소의 값을 sets 배열에 저장합니다.
            numbers.each(function (subIdx) {
                if (subIdx > 0) {
                    game += ',';
                }
                game += $(this).text();
            });

            $('#game' + (idx + 1)).val(game);
        }
    });

    const purchaseConfirmModal = new bootstrap.Modal(document.getElementById('purchase-confirm-modal'));
    purchaseConfirmModal.show();

}

purchaseButton.addEventListener('click', handlePurchaseButtonClick);

//////////////////////////////

const purchaseSpinner = document.querySelector('#purchase-spinner');
const purchaseConfirmButton = document.querySelector('#purchase-confirm-btn');

let keyDownPrevent;

/**
 * 구매 확인 modal 에서의 확인 버튼 클릭 event handler
 */
purchaseConfirmButton.addEventListener('click', function () {
    //변조로 인한 부적절한 값이 없는지 검증
    let isError = false;

    for (let i = 1; i <= 5; i++) {
        const game = document.getElementById('game' + i);
        if (game.value !== '') {
            // 정규식 패턴: 1부터 45까지의 숫자 6개가 쉼표로 구분된 문자열
            //개발자도구로 숫자 변조를 파악
            const pattern = /^(?:[1-9]|[1-3][0-9]|4[0-5])(?:,(?:[1-9]|[1-3][0-9]|4[0-5])){5}$/;
            isError |= !pattern.test(game.value); //정규식 test 실패 시, isError 는 true
        }
    }

    if (isError) {
        alert('비정상적인 접근이 감지되어 페이지를 새로고침합니다.');
        location.reload();
        return;
    }

    //변조 가능성이 있으므로 submit 직전 회차 타이머에서 draw no 가져와서 세팅
    $('#draw-no').val(drawNo);

    keyDownPrevent = true;
    purchaseSpinner.style.display = 'flex';
    $('#purchase-form').submit();
});

/**
 * key down event handler
 */
document.addEventListener('keydown', function(e) {
    if (keyDownPrevent) {
        e.preventDefault();
    }
});

//////////////////////////////

/**
 * 추가한 게임 삭제 처리
 * @param buttonElement 삭제 버튼
 */
function clearNumbers(buttonElement) {
    // 클릭한 버튼의 부모 요소인 div.item find
    const parentElement = buttonElement.parentNode;
    const balls = parentElement.getElementsByClassName('ball');

    // .ball 요소의 텍스트 지우기
    for (let i = 0; i < balls.length; i++) {
        balls[i].classList.remove(getNumberClass(balls[i].textContent));
        balls[i].classList.add('ball');

        balls[i].textContent = '';
    }

    gameCount--;

    buttonElement.disabled = true;
}
