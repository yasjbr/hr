<%--
  Created by IntelliJ IDEA.
  User: wassi
  Date: 09/08/17
  Time: 1:19
--%>


<script>


    $(document).ready(function () {
        $('#modal-form').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form'));
        });
        $('#modal-form1').on('shown.bs.modal', function () {
            _dataTables['applicantTableToChooseInRecruitment'].draw();
            gui.initAllForModal.init($('#modal-form1'));
        });
        $('#modal-form2').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form2'));
        });
        $('#modal-form3').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form3'));
        });
        $('#modal-form4').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form4'));
        });
        $('#modal-form5').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form5'));
        });
        $('#modal-form6').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form6'));
        });
    });

    /*to add applicant to Trainee list */
    function addApplicants() {
        $.ajax({
            url: "${createLink(controller: 'traineeList',action: 'addApplicants')}",
            data: $("#addApplicantIntoTraineeList").serialize(),
            type: "POST",
            dataType: "json",
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            success: function (data) {
                if (data.success) {
                    _dataTables['applicantTableInTraineeList'].draw();
                    $('#modal-form1').modal('hide');
                    guiLoading.hide();
                }
                else {
                    $('.alert.modalPage').html(data.message);
                    guiLoading.hide();
                }
            },
            error: function (xhr, status) {
            }
        });
    }


    /*add applicant with special case that status is not TRAINING_PASSED to Trainee list*/
    function addExceptionalApplicants() {
        $.ajax({
            url: "${createLink(controller: 'traineeList',action: 'addExceptionalApplicants')}",
            data: $("#addApplicantIntoTraineeListException").serialize(),
            type: "POST",
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    _dataTables['applicantTableAsSpecialCaseInTrainee'].draw();
                    _dataTables['applicantTableInTraineeList'].draw();

                    $('#modal-form2').modal('hide');
                }
                else {
                    console.log(data)
                }
            },
            error: function (xhr, status) {
            }
        });
    }


    /*to save the sent allowance list form*/
    function sendData() {
        $.ajax({
            url: "${createLink(controller: 'traineeList',action: 'sendData')}",
            data: $("#sendForm").serialize(),
            type: "POST",
            dataType: "json",
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            success: function (data) {

                //to reload the page after the ajax action
                window.location.reload();
                guiLoading.hide();
            },
            error: function (xhr, status) {
                guiLoading.hide();
                $('.alert.modalPage').html(data.message);

            }
        });
    }

    /*to save the received allowance list form*/
    function saveReceivedForm() {
        $.ajax({
            url: "${createLink(controller: 'traineeList',action: 'saveReceivedForm')}",
            data: $("#receivedForm").serialize(),
            type: "POST",
            dataType: "json",
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            success: function (data) {
                _dataTables['applicantTableInTraineeList'].draw();

                $('#fromDate').val("");
                $('#fromDate').trigger("change");

                $('#manualIncomeNo').val("");
                $('#manualIncomeNo').trigger("change");

                //to hide the modal form
                $('#modal-form4').modal('hide');
                //to reload the page after the ajax action
                window.location.reload();
                guiLoading.hide();
            },
            error: function (xhr, status) {
                alert("Sorry, there was a problem while loading data." + xhr + " ----- " + status)
            }
        });
    }


    /*to change the allowance  list employee status to accepted*/
    function changeRequestToApproved() {
        $.ajax({
            url: "${createLink(controller: 'traineeList',action: 'changeRequestToApproved')}",
            data: $("#changeApplicantToPassedForm,#applicantSearchForm").serialize(),
            type: "POST",
            dataType: "json", beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            success: function (data) {
                if (data.success) {
                    _dataTables['applicantTableInTraineeList'].draw();
                    $('#modal-form5').modal('hide');
                    guiLoading.hide();
                }
            },
            error: function (xhr, status) {
            }
        });
    }

    /*to change the request status to rejected*/
    function changeRequestToRejected() {
        $.ajax({
            url: "${createLink(controller: 'traineeList',action: 'rejectRequest')}",
            data: $("#changeApplicantToRejectForm,#applicantSearchForm").serialize(),
            type: "POST",
            dataType: "json",
            beforeSend: function (jqXHR, settings) {
                guiLoading.show();
            },
            success: function (data) {
                if (data.success) {
                    _dataTables['applicantTableInTraineeList'].draw();
                    $('#note').val("");
                    $('#note').trigger("change");
                    $('#modal-form6').modal('hide');
                    guiLoading.hide();
                }
            },
            error: function (xhr, status) {
            }
        });
    }


    /*to save the close allowance list form*/
    function closeList() {
        $.ajax({
            url: "${createLink(controller: 'traineeList',action: 'closeList')}",
            data: $("#closeForm").serialize(),
            type: "POST",
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    window.location.href = "${createLink(controller: 'traineeList',action: 'list')}";
                } else {
                    $('.alert.page').html(data.message);
                    $('#modal-form3').modal('hide');

                }
            },
            error: function (xhr, status) {
                alert("Sorry, there was a problem while loading data." + xhr + " ----- " + status)
            }
        });
    }

</script>