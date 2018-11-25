<%--
  Created by IntelliJ IDEA.
  User: rkhader
  Date: 09/05/17
  Time: 13:55
--%>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'jobRequisition.listManager.label', default: 'JobRequisition ListManager')}"/>
    <g:set var="entity"
           value="${message(code: 'jobRequisition.listManager.entities.label', default: 'JobRequisition')}"/>
    <g:set var="title"
           value="${message(code: 'jobRequisition.listManager.entities.label', args: [entities], default: 'JobRequisition ListManager')}"/>
    <title>${title}</title>
    <g:render template="script"/>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="jobRequisitionManagerCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="jobRequisitionSearchManagerForm">
            <g:render template="/jobRequisition/searchListManager" model="[:]"/>

            <el:formButton functionName="search" onClick="_dataTables['jobRequisitionManagerTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('jobRequisitionSearchManagerForm');_dataTables['jobRequisitionManagerTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<el:dataTable id="jobRequisitionManagerTable" searchFormName="jobRequisitionSearchManagerForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="jobRequisition"
              spaceBefore="true" hasRow="true"
              action="filterManager"
              domainColumns="DOMAIN_MANAGER_COLUMNS"
              serviceName="jobRequisition">

%{--show modal with note details--}%
    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="id" controller="jobRequisition" action="acceptFormModal"
            class="green  icon-ok-3" type="modal-ajax"
            message="${message(code: 'jobRequisition.accept.label', default: 'accept')}"/>

%{--show modal with note details--}%
    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="id" controller="jobRequisition" action="rejectFormModal"
            class="red  icon-cancel" type="modal-ajax"
            message="${message(code: 'jobRequisition.reject.label', default: 'accept')}"/>

</el:dataTable>


<script>

    var recordId = 0;

    $(document).ready(function () {
        $('#acceptModal').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#acceptModal'));
        });

        $('#rejectModal').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#rejectModal'));
        });
    })


    /*close modal*/
    function exitModal(id) {
        $("#" + id).hide();
    }


    /*to confirm accept job requisition*/
    function confirm() {
        var note = $("#note").val();
        var numberOfApprovedPositions = $("#acceptNumberOfApprovedPositions").val();
        $.ajax({
            url: "${createLink(controller: 'jobRequisition',action: 'setApprovedPositions')}",
            data: {id: recordId, note: note, acceptNumberOfApprovedPositions: numberOfApprovedPositions},
            type: "POST",
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    _dataTables['jobRequisitionManagerTable'].draw();
                    $("#acceptModal").hide();
                } else {
                    $('.alert.modalPage').html(data.message);
                }
            },
            error: function (xhr, status) {
            }
        });
    }


    /*to confirm reject job requisition*/
    function reject() {
        var note = $("#rejectNote").val();
        if (note != null) {
            $.ajax({
                url: "${createLink(controller: 'jobRequisition',action: 'setApprovedPositions')}",
                data: {"id": recordId, "acceptNumberOfApprovedPositions": "0", note: note},
                type: "POST",
                dataType: "json",
                success: function (data) {
                    if (data.success) {
                        _dataTables['jobRequisitionManagerTable'].draw();
                        $("#rejectModal").hide();
                    }
                    else {
                        $('.alert.modalPage').html(data.message);
                    }
                },
                error: function (xhr, status) {
                }
            });
        } else {
            $('.alert.modalPage').html('${message(code: 'jobRequisition.error.label',default: 'error ')}');
        }
    }


    /*open accept Modal and reset field */
    function openAcceptModal(row) {
        recordId = row;
        $('.alert.modalPage').html("");
        $("#note").val("");
        $("#acceptNumberOfApprovedPositions").val("");
        $("#acceptModal").show();
    }


    /*open reject Modal and reset field */
    function openRejectModal(row) {
        recordId = row;
        $('.alert.modalPage').html("");
        $("#rejectNote").val("");
        $("#rejectModal").show(row);

    }

</script>

</body>
</html>