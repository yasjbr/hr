<el:formGroup>
    <el:dateField name="effectiveDate"
                  size="6"
                  class=" isRequired"
                  label="${message(code: 'loanListPerson.effectiveDate.label')}"
                  value=""/>

</el:formGroup>


<el:row/>
<div style="padding-right: 40px;,padding-bottom: 15px;">
    <h4 class=" smaller lighter blue">
        ${message(code: 'loanList.loanListRelatedPerson.label')}
    </h4>
    <hr/>
</div>

<lay:wall>

    <div id="errorDiv" style="display: none;">
        <msg:error label="${message(code: 'loanRequest.relatedPersonError.label')}"/>
    </div>
    <br/>

    <div id="relatedPersonDiv">
        <el:formGroup>

            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="8"
                    class=" isRequired"
                    controller="person"
                    action="autocomplete"
                    name="personId"
                    label="${message(code: 'employee.searchPerson.label', default: 'employee')}"/>

            <btn:addButton onclick="addRelatedPerson()"/>
        </el:formGroup>

        <ol id="relatedPersonUl">

        </ol>

    </div>
</lay:wall>


<script type="text/javascript">

    _currentValues = new Array();
    _spanIds = new Array();
    _dataTablesCheckBoxValues=new Array();

    $(document).ready(function () {
        applyValidation("personId", false);
        getOldPersons();
    });

    function getOldPersons() {
        var id = _dataTablesCheckBoxValues['loanListPersonTable'] ? _dataTablesCheckBoxValues['loanListPersonTable'][0] : null;
        var arrayLength = _dataTablesCheckBoxValues['loanListPersonTable'] ? _dataTablesCheckBoxValues['loanListPersonTable'].length : null;
        if (arrayLength > 1) {
            id = null;
        }
        $.ajax({
            url: '${createLink(controller: 'loanList',action: 'getReceivedLoanPersonAJAX')}',
            type: 'POST',
            data: {
                id: id
            },
            dataType: 'html',
            beforeSend: function (jqXHR, settings) {
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (html) {
                guiLoading.hide();
                $('#relatedPersonUl').html(html);
            }
        });
    }

    function changeRelatedPerson() {
        if ($('#isJudgementForEver_').is(":checked")) {
            $("#relatedPersonDiv").hide();
            applyValidation("personId", false);
        } else {
            $("#relatedPersonDiv").show();
        }
    }


    function addRelatedPerson() {
        var personId = $.trim($("#personId").val());
        applyValidation("personId");
        var spanText = $('#personId').select2('data')[0].text;
        $("#relatedPersonUl .label-warning").removeClass("label-warning");
        $('#errorDiv').hide();

        var isValidRow = false;
        if (personId) {
            isValidRow = true;
        }
        if (validation.isArrayContains.call(_currentValues, spanText)) {
            $('#errorDiv').show();
            var index = validation.indexOfArrayValue(_currentValues, spanText);
            var spanId = _spanIds[index];
            $('#' + spanId).parent().addClass("label-warning");

        } else if (isValidRow) {
            var liId = "relatedPerson_" + personId;
            var spanTextId = liId + "_span";
            var newLi = $("<li id='" + liId + "'>");
            _currentValues.push(spanText);
            _spanIds.push(spanTextId);
            var indexArray = _spanIds.length - 1;
            newLi.append("<input type='hidden' name='receivedPersonId' value='" + personId + "'/>");
            newLi.append("<span id='" + spanTextId + "' >" + spanText + "</span>");
            newLi.append("<button type='button' class='close' href='#' onclick='removeDetails(" + indexArray + ")'>X</button>");
            newLi.addClass("well-sm alert alert-success");
            $("#relatedPersonUl").prepend(newLi);
            applyValidation("personId", false);
            resetDetailsData();
        }
    }

    function removeDetails(indexArray) {
        var spanId = _spanIds[indexArray];
        var liId = spanId.replace("_span", "");
        var spanText = $('#' + spanId).text();
        _currentValues = validation.removeFromArray(_currentValues, spanText);
        _spanIds = validation.removeFromArray(_spanIds, spanId);
        $('#' + liId).remove();
    }

    function resetDetailsData() {
        $("#personId").val('');
        $('#personId').trigger('change');
    }

    function applyValidation(inputId, isCheck) {
        if (isCheck == null) isCheck = true;
        var periodElement = $("#" + inputId);
        var periodElementParent = periodElement.closest(".pcp-form-control");
        if (!$.trim(periodElement.val()) && isCheck) {
            periodElement.addClass("isRequired");
            periodElementParent.addClass("has-error");
        } else {
            periodElement.removeClass("isRequired");
            periodElementParent.removeClass("isRequired");
            periodElementParent.removeClass("has-error");
        }
    }

</script>