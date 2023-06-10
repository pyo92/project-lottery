const recommendButton = document.getElementById('recommend-btn');

/**
 * 추천번호 조합 생성 버튼 처리
 */
recommendButton.addEventListener('click', function () {
    $.ajax({
        type: 'POST',
        url: '/L645/game',
        data: {
            number1: null,
            number2: null,
            number3: null,
            number4: null,
            number5: null,
            number6: null,
            drawNo: calcDrawNo(),
            combinationType: 'RECOMMEND'
        },
        success: function (response) {
            const gameGrid = document.getElementById('recommend-game-grid');

            const gameNumbers = document.createElement("div");
            gameNumbers.className = "game-numbers";

            const gameTitle = document.createElement("p");
            gameTitle.className = "label-mini-title";
            gameTitle.textContent = "Game " + (gameGrid.children.length + 1);
            gameNumbers.appendChild(gameTitle);

            const ball1 = document.createElement("span");
            ball1.className = "ball";
            ball1.classList.add(getNumberClass(response.number1));
            ball1.textContent = response.number1;
            
            const ball2 = document.createElement("span");
            ball2.className = "ball";
            ball2.classList.add(getNumberClass(response.number2));
            ball2.textContent = response.number2;
            
            const ball3 = document.createElement("span");
            ball3.className = "ball";
            ball3.classList.add(getNumberClass(response.number3));
            ball3.textContent = response.number3;
            
            const ball4 = document.createElement("span");
            ball4.className = "ball";
            ball4.classList.add(getNumberClass(response.number4));
            ball4.textContent = response.number4;
            
            const ball5 = document.createElement("span");
            ball5.className = "ball";
            ball5.classList.add(getNumberClass(response.number5));
            ball5.textContent = response.number5;
            
            const ball6 = document.createElement("span");
            ball6.className = "ball";
            ball6.classList.add(getNumberClass(response.number6));
            ball6.textContent = response.number6;

            const saveButton = document.createElement("i");
            saveButton.className = "fas fa-heart";

            gameNumbers.appendChild(ball1);
            gameNumbers.appendChild(ball2);
            gameNumbers.appendChild(ball3);
            gameNumbers.appendChild(ball4);
            gameNumbers.appendChild(ball5);
            gameNumbers.appendChild(ball6);
            gameNumbers.appendChild(saveButton);

            gameGrid.appendChild(gameNumbers);
        },
        error: function () {
            alert('로그인 세션이 만료되었습니다.')
            window.location.href = '/';
        }
    });
});

//////////////////////////////

const fixedCheckboxes = document.querySelectorAll('input[type="checkbox"][name^="fixed-number"]');
let selectedFixedNumbers = [];

/**
 * 로또 번호 OMR 체크 선택 처리
 */
fixedCheckboxes.forEach((checkbox) => {
    checkbox.addEventListener('change', function () {
        selectedFixedNumbers = [];
        fixedCheckboxes.forEach((checkbox) => {
            if (checkbox.checked) {
                selectedFixedNumbers.push(checkbox.value);
            }
        });

        if (selectedFixedNumbers.length > 6) {
            this.checked = false;
            selectedFixedNumbers.splice(selectedFixedNumbers.indexOf(this.value), 1);
            alert('더 이상 선택할 수 없습니다.');
        }
    });
});

//////////////////////////////

const fixedButton = document.getElementById('fixed-btn');

/**
 * 고정수 조합 생성 버튼 처리
 */
fixedButton.addEventListener('click', function () {
    $.ajax({
        type: 'POST',
        url: '/L645/game',
        data: {
            number1: selectedFixedNumbers[0],
            number2: selectedFixedNumbers[1],
            number3: selectedFixedNumbers[2],
            number4: selectedFixedNumbers[3],
            number5: selectedFixedNumbers[4],
            number6: selectedFixedNumbers[5],
            drawNo: calcDrawNo(),
            combinationType: 'FIXED'
        },
        success: function (response) {
            const gameGrid = document.getElementById('fixed-game-grid');

            const gameNumbers = document.createElement("div");
            gameNumbers.className = "game-numbers";

            const gameTitle = document.createElement("p");
            gameTitle.className = "label-mini-title";
            gameTitle.textContent = "Game " + (gameGrid.children.length + 1);
            gameNumbers.appendChild(gameTitle);

            const ball1 = document.createElement("span");
            ball1.className = "ball";
            ball1.classList.add(getNumberClass(response.number1));
            ball1.textContent = response.number1;

            const ball2 = document.createElement("span");
            ball2.className = "ball";
            ball2.classList.add(getNumberClass(response.number2));
            ball2.textContent = response.number2;

            const ball3 = document.createElement("span");
            ball3.className = "ball";
            ball3.classList.add(getNumberClass(response.number3));
            ball3.textContent = response.number3;

            const ball4 = document.createElement("span");
            ball4.className = "ball";
            ball4.classList.add(getNumberClass(response.number4));
            ball4.textContent = response.number4;

            const ball5 = document.createElement("span");
            ball5.className = "ball";
            ball5.classList.add(getNumberClass(response.number5));
            ball5.textContent = response.number5;

            const ball6 = document.createElement("span");
            ball6.className = "ball";
            ball6.classList.add(getNumberClass(response.number6));
            ball6.textContent = response.number6;

            const saveButton = document.createElement("i");
            saveButton.className = "fas fa-heart";

            gameNumbers.appendChild(ball1);
            gameNumbers.appendChild(ball2);
            gameNumbers.appendChild(ball3);
            gameNumbers.appendChild(ball4);
            gameNumbers.appendChild(ball5);
            gameNumbers.appendChild(ball6);
            gameNumbers.appendChild(saveButton);

            gameGrid.appendChild(gameNumbers);
        },
        error: function () {
            alert('로그인 세션이 만료되었습니다.')
            window.location.href = '/';
        }
    });
});

//////////////////////////////

const negativeCheckboxes = document.querySelectorAll('input[type="checkbox"][name^="negative-number"]');
let selectedNegativeNumbers = [];

/**
 * 로또 번호 OMR 체크 선택 처리
 */
negativeCheckboxes.forEach((checkbox) => {
    checkbox.addEventListener('change', function () {
        selectedNegativeNumbers = [];
        negativeCheckboxes.forEach((checkbox) => {
            const label = document.querySelector(`label[for='${checkbox.id}']`);
            if (checkbox.checked) {
                label.textContent = 'X';
                selectedNegativeNumbers.push(checkbox.value);
            } else {
                label.textContent = checkbox.value;
            }
        });

        if (selectedNegativeNumbers.length > 39) {
            this.checked = false;
            selectedNegativeNumbers.splice(selectedNegativeNumbers.indexOf(this.value), 1);
            const label = document.querySelector(`label[for='${checkbox.id}']`);
            label.textContent = this.value;
            alert('더 이상 선택할 수 없습니다.');
        }
    });
});

//////////////////////////////

const negativeButton = document.getElementById('negative-btn');

/**
 * 제외수 조합 생성 버튼 처리
 */
negativeButton.addEventListener('click', function () {
    $.ajax({
        type: 'POST',
        url: '/L645/game/negative',
        data: {
            number1: selectedNegativeNumbers[0],
            number2: selectedNegativeNumbers[1],
            number3: selectedNegativeNumbers[2],
            number4: selectedNegativeNumbers[3],
            number5: selectedNegativeNumbers[4],
            number6: selectedNegativeNumbers[5],
            number7: selectedNegativeNumbers[6],
            number8: selectedNegativeNumbers[7],
            number9: selectedNegativeNumbers[8],
            number10: selectedNegativeNumbers[9],
            number11: selectedNegativeNumbers[10],
            number12: selectedNegativeNumbers[11],
            number13: selectedNegativeNumbers[12],
            number14: selectedNegativeNumbers[13],
            number15: selectedNegativeNumbers[14],
            number16: selectedNegativeNumbers[15],
            number17: selectedNegativeNumbers[16],
            number18: selectedNegativeNumbers[17],
            number19: selectedNegativeNumbers[18],
            number20: selectedNegativeNumbers[19],
            number21: selectedNegativeNumbers[20],
            number22: selectedNegativeNumbers[21],
            number23: selectedNegativeNumbers[22],
            number24: selectedNegativeNumbers[23],
            number25: selectedNegativeNumbers[24],
            number26: selectedNegativeNumbers[25],
            number27: selectedNegativeNumbers[26],
            number28: selectedNegativeNumbers[27],
            number29: selectedNegativeNumbers[28],
            number30: selectedNegativeNumbers[29],
            number31: selectedNegativeNumbers[30],
            number32: selectedNegativeNumbers[31],
            number33: selectedNegativeNumbers[32],
            number34: selectedNegativeNumbers[33],
            number35: selectedNegativeNumbers[34],
            number36: selectedNegativeNumbers[35],
            number37: selectedNegativeNumbers[36],
            number38: selectedNegativeNumbers[37],
            number39: selectedNegativeNumbers[38],
            drawNo: calcDrawNo(),
            combinationType: 'NEGATIVE'
        },
        success: function (response) {
            const gameGrid = document.getElementById('negative-game-grid');

            const gameNumbers = document.createElement("div");
            gameNumbers.className = "game-numbers";

            const gameTitle = document.createElement("p");
            gameTitle.className = "label-mini-title";
            gameTitle.textContent = "Game " + (gameGrid.children.length + 1);
            gameNumbers.appendChild(gameTitle);

            const ball1 = document.createElement("span");
            ball1.className = "ball";
            ball1.classList.add(getNumberClass(response.number1));
            ball1.textContent = response.number1;

            const ball2 = document.createElement("span");
            ball2.className = "ball";
            ball2.classList.add(getNumberClass(response.number2));
            ball2.textContent = response.number2;

            const ball3 = document.createElement("span");
            ball3.className = "ball";
            ball3.classList.add(getNumberClass(response.number3));
            ball3.textContent = response.number3;

            const ball4 = document.createElement("span");
            ball4.className = "ball";
            ball4.classList.add(getNumberClass(response.number4));
            ball4.textContent = response.number4;

            const ball5 = document.createElement("span");
            ball5.className = "ball";
            ball5.classList.add(getNumberClass(response.number5));
            ball5.textContent = response.number5;

            const ball6 = document.createElement("span");
            ball6.className = "ball";
            ball6.classList.add(getNumberClass(response.number6));
            ball6.textContent = response.number6;

            const saveButton = document.createElement("i");
            saveButton.className = "fas fa-heart";

            gameNumbers.appendChild(ball1);
            gameNumbers.appendChild(ball2);
            gameNumbers.appendChild(ball3);
            gameNumbers.appendChild(ball4);
            gameNumbers.appendChild(ball5);
            gameNumbers.appendChild(ball6);
            gameNumbers.appendChild(saveButton);

            gameGrid.appendChild(gameNumbers);
        },
        error: function () {
            alert('로그인 세션이 만료되었습니다.')
            window.location.href = '/';
        }
    });
});

//////////////////////////////

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

//////////////////////////////

/**
 * 현재 일시에 따른 회차 계산 처리
 * @returns {number} 현재 회차
 */
function calcDrawNo() {
    const currentDate = new Date();

    const startDate = new Date('2002-11-30'); //시작 날짜 설정
    startDate.setHours(23, 59, 59, 999);

    const millisecondsPerWeek = 7 * 24 * 60 * 60 * 1000; //1주일에 해당하는 밀리초

    //현재 날짜와 시작 날짜 사이의 주차 계산
    let drawNo = Math.ceil((currentDate - startDate) / millisecondsPerWeek);

    const dayOfWeek = currentDate.getDay();
    const hours = currentDate.getHours();

    //판매 완료(매주 토요일 20시)인 경우 회차를 하나 증가시킴
    if (dayOfWeek === 6 && hours >= 20) {
        drawNo += 1;
    }

    return drawNo;
}
