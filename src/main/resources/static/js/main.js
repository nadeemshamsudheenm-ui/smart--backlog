/**
 * Smart Backlog Clearance Portal - Interactive JS Logic
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Demo Credentials Fast Fill
    document.querySelectorAll('.demo-account-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            const username = chip.getAttribute('data-user');
            const password = chip.getAttribute('data-pass');
            const usernameInput = document.getElementById('username');
            const passwordInput = document.getElementById('password');
            if (usernameInput && passwordInput) {
                usernameInput.value = username;
                passwordInput.value = password;
                usernameInput.focus();
            }
        });
    });

    // 2. Interactive Practice Quiz Countdown Timer
    const timerElement = document.getElementById('quiz-timer');
    if (timerElement) {
        const totalMinutes = parseInt(timerElement.getAttribute('data-duration') || '15', 10);
        let timeRemaining = totalMinutes * 60;
        const timeTakenInput = document.getElementById('timeTakenSeconds');
        const quizForm = document.getElementById('quiz-form');
        let elapsedSeconds = 0;

        const updateTimerDisplay = () => {
            const minutes = Math.floor(timeRemaining / 60);
            const seconds = timeRemaining % 60;
            timerElement.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

            if (timeRemaining <= 120) {
                timerElement.parentElement.classList.add('timer-warning');
            }

            if (timeRemaining <= 0) {
                clearInterval(interval);
                alert("Time is up! Submitting your answers automatically...");
                if (timeTakenInput) timeTakenInput.value = elapsedSeconds;
                if (quizForm) quizForm.submit();
            }
            timeRemaining--;
            elapsedSeconds++;
            if (timeTakenInput) timeTakenInput.value = elapsedSeconds;
        };

        updateTimerDisplay();
        const interval = setInterval(updateTimerDisplay, 1000);

        if (quizForm) {
            quizForm.addEventListener('submit', () => {
                clearInterval(interval);
                if (timeTakenInput) timeTakenInput.value = elapsedSeconds;
            });
        }
    }

    // 3. Quiz Question Selection & Counter tracking
    const quizForm = document.getElementById('quiz-form');
    if (quizForm) {
        const optionInputs = quizForm.querySelectorAll('.option-input');
        const answeredCounter = document.getElementById('answered-counter');
        const totalQuestions = document.querySelectorAll('.question-block').length;

        const updateAnsweredCount = () => {
            const answered = new Set();
            quizForm.querySelectorAll('.option-input:checked').forEach(input => {
                answered.add(input.name);
            });
            if (answeredCounter) {
                answeredCounter.textContent = `${answered.size} of ${totalQuestions} answered`;
            }
        };

        optionInputs.forEach(input => {
            input.addEventListener('change', updateAnsweredCount);
        });
        updateAnsweredCount();
    }
});

// Confirmation helper
function confirmAction(message) {
    return confirm(message || 'Are you sure you want to proceed?');
}
