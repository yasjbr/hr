<script>
    var index = "${(joinedFirmOperationDocument?.id)?(joinedFirmOperationDocument?.transientData?.firmDocumentOperation?.size()+1):1}";
    var documentIdList = [];


    <g:each in="${joinedFirmOperationDocument?.transientData?.documentIdList}" status="indx" var="array">
    documentIdList.push("${array}");
    </g:each>

    function afterSave() {
        $("#detailsTable tbody").empty();
        index = "${(joinedFirmOperationDocument?.id)?(joinedFirmOperationDocument?.transientData?.firmDocumentOperation?.size()+1):1}";
    }


    function openFirmDocumentModal() {
        $('.alert.modalPage').html("");
        resetForm();
        $("#firmDocumentModal").modal('show');
    }


    function resetForm() {
        gui.autocomplete.clear("firmDocumentId");
        $("#firmDocumentId").html("");
        $('input[name="isMandatory"]').removeAttr('checked');

    }


    function showError(errorMessage) {
        var msg = "<div class='alert alert-block alert-danger'>" +
                "<button data-dismiss='alert' class='close' type='button'>" +
                "<i class='ace-icon fa fa-times'>" +
                "</i>" +
                "</button>" +
                "<ul>" +
                "<li>" + errorMessage + "</li> " +
                "</ul>" +
                "</div>";
        $('.alert.modalPage').html(msg);
    }

    function showInfo(infoMessage) {
        var msg = "<div class='alert alert-block alert-success'>" +
                "<button data-dismiss='alert' class='close' type='button'>" +
                "<i class='ace-icon fa fa-check'>" +
                "</i>" +
                "</button>" +
                "<ul>" +
                "<li>" + infoMessage + "</li> " +
                "</ul>" +
                "</div>";
        $('.alert.modalPage').html("");
        $('.alert.modalPage').html(msg);
    }


    function deleteRow(index, firmDocumentId) {
        gui.confirm.confirmFunc("${message(code:'default.confirmTitle.label')}", "${message(code:'default.confirm.label')}", function () {
            documentIdList.splice(documentIdList.indexOf(firmDocumentId));
            $('#row-' + index).remove();
            resetDocumentsTable();
        });
    }

    // to put empty row when no documents added
    function resetDocumentsTable(){
        if($("#detailsTable tbody tr.center").length == 0) {
            var rowTable //= "<rowElement>";
            rowTable += "<tr id='row-0' class='center document-row' >";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "</tr>";
            $("#detailsTable").append(rowTable);
        }
    }

    // to clear documents table
    function clearDocumentsTable(){
            $("#detailsTable .document-row").remove();
            documentIdList = [];
            index = 0;
            resetDocumentsTable();
    }

    function addFirmDocument() {

        $('.alert.modalPage').html("");
        var firmDocumentName = $("#firmDocumentId").text();
        var firmDocumentId = $("#firmDocumentId").val();
        var isMandatory = $("#isMandatory").val();
        var isMandatoryBoolean = $("#isMandatory").val();
        var checkDuplicate = 0;

        documentIdList.forEach(function (entry) {
            if (entry == firmDocumentId) {
                checkDuplicate++;
            }
        });

        if (firmDocumentId <= 0) {
            showError("${message(code:'joinedFirmOperationDocument.errorFirmDocument.label')}");
        }
        else if (checkDuplicate > 0) {
            showError("${message(code:'joinedFirmOperationDocument.errorFirmDocumentDuplicate.label')}");
            resetForm();
        }
        else {
            if (isMandatory == "true") {
                isMandatory = "نعم"
            } else {
                isMandatory = "لا"
            }
            var rowTable = "<tr id='row-" + index + "' class='center document-row' >";

            rowTable += '<td class="center">' + firmDocumentName + '</td>';
            rowTable += '<td class="center">' + isMandatory + '</td>';
            rowTable += '<td class="center">';
            rowTable += '<input type="hidden" name="isMandatory"  id="isMandatory-' + index + '" value="' + isMandatoryBoolean + '">';
            rowTable += '<input type="hidden" name="firmDocument"  id="firmDocument-' + index + '" value="' + $("#firmDocumentId").val() + '">';
            rowTable += '<span class="delete-action">';
            rowTable += '<a style="cursor: pointer;" class="red icon-trash" onclick="deleteRow('
                    + index + ",'"  +  $("#firmDocumentId").val()  + "'" +
                    ')" title="<g:message code='default.button.delete.label'/>">';
            rowTable += '</a> </span>';
            rowTable += '</td></tr>';

            //end actions
            jQuery("#detailsTable tbody").append(rowTable);
            documentIdList.push(firmDocumentId);
            showInfo("${message(code:'joinedFirmOperationDocument.firmDocument.modal.add.success')}");
            index++;
            $('#row-0').remove();
            resetForm();
        }
    }

    function closeFirmDocumentModal() {
        resetForm();
        $("#firmDocumentModal").modal('hide');
    }



</script>