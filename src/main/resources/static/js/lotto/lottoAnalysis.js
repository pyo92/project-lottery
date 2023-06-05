const startDrawNoAc = document.getElementById('start-draw-no-ac');
const endDrawNoAc = document.getElementById('end-draw-no-ac');
const analysisAcButton = document.getElementById('btn-analysis-ac');

/**
 * 회차별 AC 통계 조회 처리
 */
analysisAcButton.addEventListener('click', function () {
    if (parseInt(startDrawNoAc.value) > parseInt(endDrawNoAc.value)) {
        alert('시작 회차는 종료 회차보다 클 수 없습니다.');
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/L645/analysis/number-arithmetic-complexity',
        data: {
            startDrawNo: startDrawNoAc.value,
            endDrawNo: endDrawNoAc.value
        },
        success: function (response) {
            const analysisAc = document.querySelector('#analysis-content-ac');
            const winInfoGrid = analysisAc.querySelector('.win-info-grid');

            while (winInfoGrid.firstChild) {
                winInfoGrid.removeChild(winInfoGrid.firstChild);
            }

            let chartData = [];

            for (let i = 0; i < response.length; i++) {
                const winInfo = document.createElement('div');
                winInfo.classList.add('win-info');

                const drawNo = document.createElement('span');
                drawNo.classList.add('label-mini-title');
                drawNo.textContent = response[i].drawNo;

                const number1 = document.createElement('span');
                number1.classList.add('ball');
                number1.classList.add(getNumberClass(response[i].number1));
                number1.textContent = response[i].number1;

                const number2 = document.createElement('span');
                number2.classList.add('ball');
                number2.classList.add(getNumberClass(response[i].number2));
                number2.textContent = response[i].number2;

                const number3 = document.createElement('span');
                number3.classList.add('ball');
                number3.classList.add(getNumberClass(response[i].number3));
                number3.textContent = response[i].number3;

                const number4 = document.createElement('span');
                number4.classList.add('ball');
                number4.classList.add(getNumberClass(response[i].number4));
                number4.textContent = response[i].number4;

                const number5 = document.createElement('span');
                number5.classList.add('ball');
                number5.classList.add(getNumberClass(response[i].number5));
                number5.textContent = response[i].number5;

                const number6 = document.createElement('span');
                number6.classList.add('ball');
                number6.classList.add(getNumberClass(response[i].number6));
                number6.textContent = response[i].number6;

                const ac = document.createElement('span');
                ac.classList.add('label-mini-title');
                ac.textContent = response[i].ac;

                winInfo.appendChild(drawNo);
                winInfo.appendChild(number1);
                winInfo.appendChild(number2);
                winInfo.appendChild(number3);
                winInfo.appendChild(number4);
                winInfo.appendChild(number5);
                winInfo.appendChild(number6);
                winInfo.appendChild(ac);

                winInfoGrid.appendChild(winInfo);

                chartData.push({
                    round: response[response.length - 1 - i].drawNo,
                    ac: response[response.length - 1 - i].ac
                });
            }

            updateLineChartAc(chartData);
        },
        error: function () {
            alert('로그인 세션이 만료되었습니다.')
            window.location.href = '/';
        }
    });
});

const analysisAllAcButton = document.getElementById('btn-analysis-all-ac');

/**
 * 회차별 AC 통계 조회 - 전체 회차 빠른 선택 버튼
 */
analysisAllAcButton.addEventListener('click', function () {
    startDrawNoAc.selectedIndex = startDrawNoAc.options.length - 1;
    endDrawNoAc.selectedIndex = 0;
    analysisAcButton.click();
});

const analysisLatest5AcButton = document.getElementById('btn-analysis-latest5-ac');

/**
 * 회차별 AC 통계 조회 - 최근 5회차 빠른 선택 버튼
 */
analysisLatest5AcButton.addEventListener('click', function () {
    startDrawNoAc.selectedIndex = 4;
    endDrawNoAc.selectedIndex = 0;
    analysisAcButton.click();
});

const analysisLatest10AcButton = document.getElementById('btn-analysis-latest10-ac');

/**
 * 회차별 AC 통계 조회 - 최근 10회차 빠른 선택 버튼
 */
analysisLatest10AcButton.addEventListener('click', function () {
    startDrawNoAc.selectedIndex = 9;
    endDrawNoAc.selectedIndex = 0;
    analysisAcButton.click();
});

const analysisLatest15AcButton = document.getElementById('btn-analysis-latest15-ac');

/**
 * 회차별 AC 통계 조회 - 최근 15회차 빠른 선택 버튼
 */
analysisLatest15AcButton.addEventListener('click', function () {
    startDrawNoAc.selectedIndex = 14;
    endDrawNoAc.selectedIndex = 0;
    analysisAcButton.click();
});

//////////////////////////////

const startDrawNoSum = document.getElementById('start-draw-no-sum');
const endDrawNoSum = document.getElementById('end-draw-no-sum');
const analysisSumButton = document.getElementById('btn-analysis-sum');

/**
 * 회차별 총합 통계 조회 처리
 */
analysisSumButton.addEventListener('click', function () {
    if (parseInt(startDrawNoSum.value) > parseInt(endDrawNoSum.value)) {
        alert('시작 회차는 종료 회차보다 클 수 없습니다.');
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/L645/analysis/number-sum',
        data: {
            startDrawNo: startDrawNoSum.value,
            endDrawNo: endDrawNoSum.value
        },
        success: function (response) {
            const analysisSum = document.querySelector('#analysis-content-sum');
            const winInfoGrid = analysisSum.querySelector('.win-info-grid');

            while (winInfoGrid.firstChild) {
                winInfoGrid.removeChild(winInfoGrid.firstChild);
            }

            let chartData = [];

            for (let i = 0; i < response.length; i++) {
                const winInfo = document.createElement('div');
                winInfo.classList.add('win-info');

                const drawNo = document.createElement('span');
                drawNo.classList.add('label-mini-title');
                drawNo.textContent = response[i].drawNo;

                const number1 = document.createElement('span');
                number1.classList.add('ball');
                number1.classList.add(getNumberClass(response[i].number1));
                number1.textContent = response[i].number1;

                const number2 = document.createElement('span');
                number2.classList.add('ball');
                number2.classList.add(getNumberClass(response[i].number2));
                number2.textContent = response[i].number2;

                const number3 = document.createElement('span');
                number3.classList.add('ball');
                number3.classList.add(getNumberClass(response[i].number3));
                number3.textContent = response[i].number3;

                const number4 = document.createElement('span');
                number4.classList.add('ball');
                number4.classList.add(getNumberClass(response[i].number4));
                number4.textContent = response[i].number4;

                const number5 = document.createElement('span');
                number5.classList.add('ball');
                number5.classList.add(getNumberClass(response[i].number5));
                number5.textContent = response[i].number5;

                const number6 = document.createElement('span');
                number6.classList.add('ball');
                number6.classList.add(getNumberClass(response[i].number6));
                number6.textContent = response[i].number6;

                const sum = document.createElement('span');
                sum.classList.add('label-mini-title');
                sum.textContent = response[i].sum;

                winInfo.appendChild(drawNo);
                winInfo.appendChild(number1);
                winInfo.appendChild(number2);
                winInfo.appendChild(number3);
                winInfo.appendChild(number4);
                winInfo.appendChild(number5);
                winInfo.appendChild(number6);
                winInfo.appendChild(sum);

                winInfoGrid.appendChild(winInfo);

                chartData.push({
                    round: response[response.length - 1 - i].drawNo,
                    sum: response[response.length - 1 - i].sum
                });
            }

            updateLineChartSum(chartData);
        },
        error: function () {
            alert('로그인 세션이 만료되었습니다.')
            window.location.href = '/';
        }
    });
});

const analysisAllSumButton = document.getElementById('btn-analysis-all-sum');

/**
 * 회차별 총합 통계 조회 - 전체 회차 빠른 선택 버튼
 */
analysisAllSumButton.addEventListener('click', function () {
    startDrawNoSum.selectedIndex = startDrawNoSum.options.length - 1;
    endDrawNoSum.selectedIndex = 0;
    analysisSumButton.click();
});

const analysisLatest5SumButton = document.getElementById('btn-analysis-latest5-sum');

/**
 * 회차별 총합 통계 조회 - 최근 5회차 빠른 선택 버튼
 */
analysisLatest5SumButton.addEventListener('click', function () {
    startDrawNoSum.selectedIndex = 4;
    endDrawNoSum.selectedIndex = 0;
    analysisSumButton.click();
});

const analysisLatest10SumButton = document.getElementById('btn-analysis-latest10-sum');

/**
 * 회차별 총합 통계 조회 - 최근 10회차 빠른 선택 버튼
 */
analysisLatest10SumButton.addEventListener('click', function () {
    startDrawNoSum.selectedIndex = 9;
    endDrawNoSum.selectedIndex = 0;
    analysisSumButton.click();
});

const analysisLatest15SumButton = document.getElementById('btn-analysis-latest15-sum');

/**
 * 회차별 총합 통계 조회 - 최근 15회차 빠른 선택 버튼
 */
analysisLatest15SumButton.addEventListener('click', function () {
    startDrawNoSum.selectedIndex = 14;
    endDrawNoSum.selectedIndex = 0;
    analysisSumButton.click();
});

//////////////////////////////

const startDrawNoNhc = document.getElementById('start-draw-no-nhc');
const endDrawNoNhc = document.getElementById('end-draw-no-nhc');
const bonusYnNhc = document.getElementById('bonus-yn-nhc');
const analysisNhcButton = document.getElementById('btn-analysis-nhc');

/**
 * 번호별 출현 횟수 통계 조회 처리
 */
analysisNhcButton.addEventListener('click', function () {
    if (parseInt(startDrawNoNhc.value) > parseInt(endDrawNoNhc.value)) {
        alert('시작 회차는 종료 회차보다 클 수 없습니다.');
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/L645/analysis/number-hit-count',
        data: {
            startDrawNo: startDrawNoNhc.value,
            endDrawNo: endDrawNoNhc.value,
            bonus: bonusYnNhc.checked
        },
        success: function (response) {
            const maxCount = Math.max(...response.map(item => item.count));
            const maxBonus = Math.max(...response.map(item => item.bonus));
            const analysisNhc = document.querySelector('#analysis-content-nhc');
            const ballInfoGrid = analysisNhc.querySelector('.ball-info-grid');

            while (ballInfoGrid.firstChild) {
                ballInfoGrid.removeChild(ballInfoGrid.firstChild);
            }

            for (let i = 0; i < response.length; i++) {
                const ballInfo = document.createElement('div');
                ballInfo.classList.add('ball-info');

                const ball = document.createElement('span');
                ball.classList.add('ball');
                ball.classList.add(getNumberClass(response[i].number));
                ball.textContent = response[i].number;

                const ballCountBar = document.createElement('div');
                ballCountBar.classList.add('ball-count-bar');

                const barFill = document.createElement('div');
                barFill.classList.add('bar-fill');

                const barFillCount = document.createElement('div');
                barFillCount.classList.add('bar-fill-count');
                barFillCount.classList.add('label-mini-title');
                barFillCount.classList.add(getNumberClass(response[i].number));
                barFillCount.style.width = (response[i].count / (maxCount + maxBonus)) * 100 + '%';
                barFillCount.style.display = response[i].count === 0 ? 'none' : 'inline-block';
                barFillCount.style.borderRadius = bonusYnNhc.checked && response[i].bonus > 0 ? '5px 0 0 5px' : '5px';
                barFillCount.textContent = response[i].count;

                const barFillBonus = document.createElement('div');
                barFillBonus.classList.add('bar-fill-bonus');
                barFillBonus.classList.add('label-mini-title');
                barFillBonus.style.width = (response[i].bonus / (maxCount + maxBonus)) * 100 + '%';
                barFillBonus.style.display = response[i].bonus === 0 ? 'none' : 'inline-block';
                barFillBonus.style.borderRadius = response[i].count === 0 ? '5px' : '0 5px 5px 0';
                barFillBonus.textContent = response[i].bonus;

                const barLabel = document.createElement('span');
                barLabel.classList.add('bar-label');
                barLabel.classList.add('label-mini-title');
                barLabel.textContent = (response[i].count + response[i].bonus).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + '회';

                barFill.appendChild(barFillCount);
                barFill.appendChild(barFillBonus);

                ballCountBar.appendChild(barFill);
                ballCountBar.appendChild(barLabel);

                ballInfo.appendChild(ball);
                ballInfo.appendChild(ballCountBar);

                ballInfoGrid.appendChild(ballInfo);
            }
        },
        error: function () {
            alert('로그인 세션이 만료되었습니다.')
            window.location.href = '/';
        }
    });
});

const analysisAllNhcButton = document.getElementById('btn-analysis-all-nhc');

/**
 * 번호별 출현 횟수 통계 조회 - 전체 회차 빠른 선택 버튼
 */
analysisAllNhcButton.addEventListener('click', function () {
    startDrawNoNhc.selectedIndex = startDrawNoNhc.options.length - 1;
    endDrawNoNhc.selectedIndex = 0;
    analysisNhcButton.click();
});

const analysisLatest5NhcButton = document.getElementById('btn-analysis-latest5-nhc');

/**
 * 번호별 출현 횟수 통계 조회 - 최근 5회차 빠른 선택 버튼
 */
analysisLatest5NhcButton.addEventListener('click', function () {
    startDrawNoNhc.selectedIndex = 4;
    endDrawNoNhc.selectedIndex = 0;
    analysisNhcButton.click();
});

const analysisLatest10NhcButton = document.getElementById('btn-analysis-latest10-nhc');

/**
 * 번호별 출현 횟수 통계 조회 - 최근 10회차 빠른 선택 버튼
 */
analysisLatest10NhcButton.addEventListener('click', function () {
    startDrawNoNhc.selectedIndex = 9;
    endDrawNoNhc.selectedIndex = 0;
    analysisNhcButton.click();
});

const analysisLatest15NhcButton = document.getElementById('btn-analysis-latest15-nhc');

/**
 * 번호별 출현 횟수 통계 조회 - 최근 15회차 빠른 선택 버튼
 */
analysisLatest15NhcButton.addEventListener('click', function () {
    startDrawNoNhc.selectedIndex = 14;
    endDrawNoNhc.selectedIndex = 0;
    analysisNhcButton.click();
});

//////////////////////////////

const startDrawNoNrhc = document.getElementById('start-draw-no-nrhc');
const endDrawNoNrhc = document.getElementById('end-draw-no-nrhc');
const bonusYnNrhc = document.getElementById('bonus-yn-nrhc');
const analysisNrhcButton = document.getElementById('btn-analysis-nrhc');

/**
 * 번호 대역별 색상 통계 조회 처리
 */
analysisNrhcButton.addEventListener('click', function () {
    if (parseInt(startDrawNoNrhc.value) > parseInt(endDrawNoNrhc.value)) {
        alert('시작 회차는 종료 회차보다 클 수 없습니다.');
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/L645/analysis/number-range-hit-count',
        data: {
            startDrawNo: startDrawNoNrhc.value,
            endDrawNo: endDrawNoNrhc.value,
            bonus: bonusYnNrhc.checked
        },
        success: function (response) {
            const maxCount = Math.max(...response.map(item => item.count));
            const maxBonus = Math.max(...response.map(item => item.bonus));
            const analysisNrhc = document.querySelector('#analysis-content-nrhc');
            const ballInfoGrid = analysisNrhc.querySelector('.ball-info-grid');

            while (ballInfoGrid.firstChild) {
                ballInfoGrid.removeChild(ballInfoGrid.firstChild);
            }

            for (let i = 0; i < response.length; i++) {
                const ballInfo = document.createElement('div');
                ballInfo.classList.add('ball-info');

                const ball = document.createElement('span');
                ball.classList.add('ball');
                ball.classList.add(getNumberClass(response[i].numberRange));
                // ball.textContent = response[i].numberRange;

                const ballCountBar = document.createElement('div');
                ballCountBar.classList.add('ball-count-bar');

                const barFill = document.createElement('div');
                barFill.classList.add('bar-fill');

                const barFillCount = document.createElement('div');
                barFillCount.classList.add('bar-fill-count');
                barFillCount.classList.add('label-mini-title');
                barFillCount.classList.add(getNumberClass(response[i].numberRange));
                barFillCount.style.width = (response[i].count / (maxCount + maxBonus)) * 100 + '%';
                barFillCount.style.display = response[i].count === 0 ? 'none' : 'inline-block';
                barFillCount.style.borderRadius = bonusYnNrhc.checked && response[i].bonus > 0 ? '5px 0 0 5px' : '5px';
                barFillCount.textContent = response[i].count;

                const barFillBonus = document.createElement('div');
                barFillBonus.classList.add('bar-fill-bonus');
                barFillBonus.classList.add('label-mini-title');
                barFillBonus.style.width = (response[i].bonus / (maxCount + maxBonus)) * 100 + '%';
                barFillBonus.style.display = response[i].bonus === 0 ? 'none' : 'inline-block';
                barFillBonus.style.borderRadius = response[i].count === 0 ? '5px' : '0 5px 5px 0';
                barFillBonus.textContent = response[i].bonus;

                const barLabel = document.createElement('span');
                barLabel.classList.add('bar-label');
                barLabel.classList.add('label-mini-title');
                barLabel.textContent = (response[i].count + response[i].bonus).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + '회';

                barFill.appendChild(barFillCount);
                barFill.appendChild(barFillBonus);

                ballCountBar.appendChild(barFill);
                ballCountBar.appendChild(barLabel);

                ballInfo.appendChild(ball);
                ballInfo.appendChild(ballCountBar);

                ballInfoGrid.appendChild(ballInfo);
            }
        },
        error: function () {
            alert('로그인 세션이 만료되었습니다.')
            window.location.href = '/';
        }
    });
});

const analysisAllNrhcButton = document.getElementById('btn-analysis-all-nrhc');

/**
 * 번호 대역별 색상 통계 조회 - 전체 회차 빠른 선택 버튼
 */
analysisAllNrhcButton.addEventListener('click', function () {
    startDrawNoNrhc.selectedIndex = startDrawNoNrhc.options.length - 1;
    endDrawNoNrhc.selectedIndex = 0;
    analysisNrhcButton.click();
});

const analysisLatest5NrhcButton = document.getElementById('btn-analysis-latest5-nrhc');

/**
 * 번호 대역별 색상 통계 조회 - 최근 5회차 빠른 선택 버튼
 */
analysisLatest5NrhcButton.addEventListener('click', function () {
    startDrawNoNrhc.selectedIndex = 4;
    endDrawNoNrhc.selectedIndex = 0;
    analysisNrhcButton.click();
});

const analysisLatest10NrhcButton = document.getElementById('btn-analysis-latest10-nrhc');

/**
 * 번호 대역별 색상 통계 조회 - 최근 10회차 빠른 선택 버튼
 */
analysisLatest10NrhcButton.addEventListener('click', function () {
    startDrawNoNrhc.selectedIndex = 9;
    endDrawNoNrhc.selectedIndex = 0;
    analysisNrhcButton.click();
});

const analysisLatest15NrhcButton = document.getElementById('btn-analysis-latest15-nrhc');

/**
 * 번호 대역별 색상 통계 조회 - 최근 15회차 빠른 선택 버튼
 */
analysisLatest15NrhcButton.addEventListener('click', function () {
    startDrawNoNrhc.selectedIndex = 14;
    endDrawNoNrhc.selectedIndex = 0;
    analysisNrhcButton.click();
});

//////////////////////////////

const startDrawNoNmc = document.getElementById('start-draw-no-nmc');
const endDrawNoNmc = document.getElementById('end-draw-no-nmc');
const bonusYnNmc = document.getElementById('bonus-yn-nmc');
const analysisNmcButton = document.getElementById('btn-analysis-nmc');

/**
 * 번호별 미출현 기간 통계 조회 처리
 */
analysisNmcButton.addEventListener('click', function () {
    if (parseInt(startDrawNoNmc.value) > parseInt(endDrawNoNmc.value)) {
        alert('시작 회차는 종료 회차보다 클 수 없습니다.');
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/L645/analysis/number-miss-count',
        data: {
            startDrawNo: startDrawNoNmc.value,
            endDrawNo: endDrawNoNmc.value,
            bonus: bonusYnNmc.checked
        },
        success: function (response) {
            const maxCount = Math.max(...response.map(item => item.count));
            const maxBonus = Math.max(...response.map(item => item.bonus));
            const analysisNmc = document.querySelector('#analysis-content-nmc');
            const ballInfoGrid = analysisNmc.querySelector('.ball-info-grid');

            while (ballInfoGrid.firstChild) {
                ballInfoGrid.removeChild(ballInfoGrid.firstChild);
            }

            for (let i = 0; i < response.length; i++) {
                const ballInfo = document.createElement('div');
                ballInfo.classList.add('ball-info');

                const ball = document.createElement('span');
                ball.classList.add('ball');
                ball.classList.add(getNumberClass(response[i].number));
                ball.textContent = response[i].number;

                const ballCountBar = document.createElement('div');
                ballCountBar.classList.add('ball-count-bar');

                const barFill = document.createElement('div');
                barFill.classList.add('bar-fill');

                const barFillCount = document.createElement('div');
                barFillCount.classList.add('bar-fill-count');
                barFillCount.classList.add('label-mini-title');
                barFillCount.classList.add(getNumberClass(response[i].number));
                barFillCount.style.width = (response[i].count / (maxCount > maxBonus ? maxCount : maxBonus)) * 100 + '%';
                barFillCount.style.borderRadius = '5px';
                barFillCount.textContent = response[i].count === 0 ? '　' : response[i].count;

                const barFillBonus = document.createElement('div');
                barFillBonus.classList.add('bar-fill-bonus');
                barFillBonus.classList.add('label-mini-title');
                barFillBonus.classList.add(getNumberClass(response[i].number));
                barFillBonus.style.width = (response[i].bonus / (maxCount > maxBonus ? maxCount : maxBonus)) * 100 + '%';
                barFillBonus.style.borderRadius = '5px';
                barFillBonus.textContent = response[i].bonus === 0 ? '　' : response[i].bonus;

                const barLabel = document.createElement('span');
                barLabel.classList.add('bar-label');
                barLabel.classList.add('label-mini-title');
                barLabel.textContent = (bonusYnNmc.checked ? response[i].bonus : response[i].count).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + '주';

                barFill.appendChild(barFillCount);
                if (bonusYnNmc.checked) barFill.appendChild(barFillBonus);

                ballCountBar.appendChild(barFill);
                ballCountBar.appendChild(barLabel);

                ballInfo.appendChild(ball);
                ballInfo.appendChild(ballCountBar);

                ballInfoGrid.appendChild(ballInfo);
            }
        },
        error: function () {
            alert('로그인 세션이 만료되었습니다.')
            window.location.href = '/';
        }
    });
});

const analysisAllNmcButton = document.getElementById('btn-analysis-all-nmc');

/**
 * 번호별 미출현 기간 통계 조회 - 전체 회차 빠른 선택 버튼
 */
analysisAllNmcButton.addEventListener('click', function () {
    startDrawNoNmc.selectedIndex = startDrawNoNmc.options.length - 1;
    endDrawNoNmc.selectedIndex = 0;
    analysisNmcButton.click();
});

const analysisLatest5NmcButton = document.getElementById('btn-analysis-latest5-nmc');

/**
 * 번호별 미출현 기간 통계 조회 - 최근 5회차 빠른 선택 버튼
 */
analysisLatest5NmcButton.addEventListener('click', function () {
    startDrawNoNmc.selectedIndex = 4;
    endDrawNoNmc.selectedIndex = 0;
    analysisNmcButton.click();
});

const analysisLatest10NmcButton = document.getElementById('btn-analysis-latest10-nmc');

/**
 * 번호별 미출현 기간 통계 조회 - 최근 10회차 빠른 선택 버튼
 */
analysisLatest10NmcButton.addEventListener('click', function () {
    startDrawNoNmc.selectedIndex = 9;
    endDrawNoNmc.selectedIndex = 0;
    analysisNmcButton.click();
});

const analysisLatest15NmcButton = document.getElementById('btn-analysis-latest15-nmc');

/**
 * 번호별 미출현 기간 통계 조회 - 최근 15회차 빠른 선택 버튼
 */
analysisLatest15NmcButton.addEventListener('click', function () {
    startDrawNoNmc.selectedIndex = 14;
    endDrawNoNmc.selectedIndex = 0;
    analysisNmcButton.click();
});

//////////////////////////////

/**
 * ball class 계산 및 부여
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

const ctxLineChartAc = document.getElementById('line-chart-ac').getContext('2d');
const lineChartAc = new Chart(ctxLineChartAc, {
    type: 'line',
    data: [],
    options: {
        responsive: true,
        plugins: {
            zoom: {
                pan: {
                    enabled: true,
                    mode: 'x',
                    speed: 10
                },
                zoom: {
                    wheel: {
                        enabled: true
                    },
                    pinch: {
                        enabled: true
                    },
                    mode: 'x'
                }
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                suggestedMax: 10,
                ticks: {
                    stepSize: 2,
                    autoSkip: false
                }
            }
        }
    }
});

/**
 * 회차별 AC 값을 차트 데이터로 변환
 */
function convertDataToLineChartAc(data) {
    return {
        labels: data.map(function (item) {
            return item.round;
        }),
        datasets: [{
            label: 'AC',
            data: data.map(function (item) {
                return item.ac;
            }),
            borderColor: 'rgba(255, 99, 132, 1)',
            backgroundColor: 'rgba(255, 99, 132, 0.2)',
            borderWidth: 1,
            pointRadius: 1
        }]
    };
}

/**
 * 회차별 AC 차트 업데이트
 */
function updateLineChartAc(data) {
    lineChartAc.data = convertDataToLineChartAc(data);
    lineChartAc.resetZoom();
    lineChartAc.update(); // 차트를 다시 그려서 업데이트된 데이터와 y 축 범위를 반영
}

//////////////////////////////

const ctxLineChartSum = document.getElementById('line-chart-sum').getContext('2d');
const lineChartSum = new Chart(ctxLineChartSum, {
    type: 'line',
    data: [],
    options: {
        responsive: true,
        plugins: {
            zoom: {
                pan: {
                    enabled: true,
                    mode: 'x',
                    speed: 10
                },
                zoom: {
                    wheel: {
                        enabled: true
                    },
                    pinch: {
                        enabled: true
                    },
                    mode: 'x'
                }
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                suggestedMax: 255
            }
        }
    }
});

/**
 * 회차별 총합 값을 차트 데이터로 변환
 */
function convertDataToLineChartSum(data) {
    return {
        labels: data.map(function (item) {
            return item.round;
        }),
        datasets: [{
            label: 'SUM',
            data: data.map(function (item) {
                return item.sum;
            }),
            borderColor: 'rgba(255, 99, 132, 1)',
            backgroundColor: 'rgba(255, 99, 132, 0.2)',
            borderWidth: 1,
            pointRadius: 1
        }]
    };
}

/**
 * 회차별 총합 차트 업데이트
 */
function updateLineChartSum(data) {
    lineChartSum.data = convertDataToLineChartSum(data);
    lineChartSum.resetZoom();
    lineChartSum.update(); // 차트를 다시 그려서 업데이트된 데이터와 y 축 범위를 반영
}
