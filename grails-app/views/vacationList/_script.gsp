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
            _dataTables['vacationRequestTableToChooseInVacation'].draw();
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

    /*to add vacationRequest to vacation list */
    function addVacationRequest() {
        $.ajax({
            url: "${createLink(controller: 'vacationList',action: 'addVacationRequests')}",
            data: $("#addVacationRequestIntoVacationList").serialize(),
            type: "POST",
            dataType: "json",
            beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            success: function (data) {
                if (data.success) {
                    _dataTables['vacationRequestTableInVacationList'].draw();
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


    /*to change the allowance  list employee status to accepted*/
    function changeRequestToApproved() {
        $.ajax({
            url: "${createLink(controller: 'vacationList',action: 'changeRequestToApproved')}",
            data: $("#changeVacationRequestToPassedForm,#vacationRequestSearchForm").serialize(),
            type: "POST",
            dataType: "json", beforeSend: function (jqXHR, settings) {
                $('.alert.page').html('');
                guiLoading.show();
            },
            success: function (data) {
                if (data.success) {
                    _dataTables['vacationRequestTableInVacationList'].draw();
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
            url: "${createLink(controller: 'vacationList',action: 'rejectRequest')}",
            data: $("#changeVacationRequestToRejectForm,#vacationRequestSearchForm").serialize(),
            type: "POST",
            dataType: "json",
            beforeSend: function (jqXHR, settings) {
                guiLoading.show();
            },
            success: function (data) {
                if (data.success) {
                    _dataTables['vacationRequestTableInVacationList'].draw();
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
            url: "${createLink(controller: 'vacationList',action: 'closeList')}",
            data: $("#closeForm").serialize(),
            type: "POST",
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    window.location.href = "${createLink(controller: 'vacationList',action: 'list')}";
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


    /*allow delete when recordStatus is NEW  */
    function manageExecuteActions(row) {
        testRowList.push(row);
        if (${vacationList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}) {
            return true;
        }
        return false;
    }


    /*to allow add note to request until close the list*/
    function showNoteInList() {
        if (${vacationList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CLOSED}) {
            return false
        }
        return true
    }


    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject',"${referenceObject}" );
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList',  "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }
</script>