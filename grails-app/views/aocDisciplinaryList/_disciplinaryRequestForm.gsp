
<el:hiddenField name="employee.id" value="${disciplinaryRequest?.employee?.id}"/>
<el:hiddenField name="employeeViolationIds"
                value="${disciplinaryRequest?.joinedDisciplinaryEmployeeViolations?.employeeViolation?.id?.unique() ?: []}"/>

<g:render template="/employee/wrapperForm" model="[employee: disciplinaryRequest?.employee]"/>

<msg:warning label="${message(code: 'disciplinaryRequest.employeeViolationInfo.label')}"/>


<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>

        <el:formGroup>

            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                             controller="disciplinaryCategory" action="autocomplete" id="disciplinaryCategoryFormId"
                             name="disciplinaryCategory.id"
                             label="${message(code: 'disciplinaryRequest.disciplinaryCategory.label',
                                     default: 'disciplinaryCategory')}"
                             values="${[[disciplinaryRequest?.disciplinaryCategory?.id,
                                         disciplinaryRequest?.disciplinaryCategory?.descriptionInfo?.localName]]}"/>

            <el:dateField name="requestDate" size="6" class=" isRequired" isMaxDate="true"
                          label="${message(code: 'disciplinaryRequest.requestDate.label', default: 'request date')}"
                          value="${disciplinaryRequest?.requestDate ?: java.time.ZonedDateTime.now()}"/>

        </el:formGroup>

        <el:formGroup>

            <el:textField name="requestReason" size="6" class=""
                          label="${message(code: 'disciplinaryRequest.requestReason.label', default: 'requestReason')}"
                          value="${disciplinaryRequest?.requestReason}"/>

            <el:textArea name="requestStatusNote" size="6"
                         class=""
                         label="${message(code: 'disciplinaryRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${disciplinaryRequest?.requestStatusNote}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>

<lay:widget transparent="true" color="blue" icon="icon-info-4"
            title="${g.message(code: "disciplinaryRequest.violationsAndJudgments.label")}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <el:modalLink id="employeeViolationLink" link="#" style="display: none;"
                          preventCloseOutSide="true" class="btn btn-sm btn-info width-135"
                          label="${message(code: 'employeeViolation.entities')}">
                <i class="icon-list"></i>
            </el:modalLink>
            <button type="button" class=" btn btn-sm btn-info width-135" onclick="viewEmployeeViolation()">
                ${message(code: 'employeeViolation.entities')}
                <i class="ace-icon icon-list"></i>
            </button>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <div id="widgetBodyViolationsAndJudgments">
            <el:formGroup id="disciplinaryReasonsFormGroup">
                <el:labelField label="${message(code: 'disciplinaryRequest.disciplinaryReasons.label')}" value=""
                               size="6"/>
            </el:formGroup>
            <el:formGroup id="disciplinaryJudgmentsFormGroup">
                <el:labelField label="${message(code: 'disciplinaryRequest.disciplinaryJudgments.label')}" value=""
                               size="6"/>
            </el:formGroup>
        </div>


        <g:if test="${workflowPathHeader}">
            <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
        </g:if>
    </lay:widgetBody>
</lay:widget>

<div id="employeeViolations" style="display: none">
    <g:render template="/aocDisciplinaryList/listViolationsModal" model="[employeeId:disciplinaryRequest?.employee?.id]"/>
</div>

<script type="text/javascript">

    var violationCount = 0;
    var disciplinaryCount = 0;
    var employeeViolationIdsList = [];

    function getViolationsWithJudgments() {
        var disciplinaryCategoryId = $('#disciplinaryCategoryFormId').val();
        var disciplinaryRequestId;
        <g:if test="${disciplinaryRequest?.id}">
        disciplinaryCategoryId = "${disciplinaryRequest?.disciplinaryCategory?.id}";
        disciplinaryRequestId = "${disciplinaryRequest?.id}";
        </g:if>

        var employeeViolationIds = $('#employeeViolationIds').val();
        var employeeId = "${disciplinaryRequest?.employee?.id}";
        if (disciplinaryCategoryId && employeeViolationIds) {
            $.ajax({
                url: '${createLink(controller: 'disciplinaryRequest',action: 'getViolationsWithJudgments')}',
                type: 'POST',
                data: {
                    employeeId: employeeId,
                    disciplinaryCategoryId: disciplinaryCategoryId,
                    "employeeViolationIds[]": employeeViolationIds,
                    disciplinaryRequestId: disciplinaryRequestId,
                    violationCount: violationCount
                },
                dataType: 'html',
                beforeSend: function (jqXHR, settings) {
                    guiLoading.show();
                }

                ,
                error: function (jqXHR) {
                    guiLoading.hide();
                }
                ,
                success: function (html) {
                    guiLoading.hide();

                    if (violationCount == 0) {
                        $('#widgetBodyViolationsAndJudgments').html("");
                    }
                    $('#widgetBodyViolationsAndJudgments').append(html);
                    violationCount++;


                    <g:if test="${disciplinaryRequest?.id}">
                    $('#disciplinaryJudgmentsFormGroup').find("input:checkbox").each(function () {
                        var input = $(this);
                        if (input.is(':checked')) {
                            viewDisciplinaryJudgmentInputs(input.attr("id"), input.attr("name"), violationCount);
                        }
                    });
                    </g:if>
                }
            })
            ;
        }
    }


    function viewDisciplinaryJudgmentInputs(id, name, violationCount) {
        var input = $('#' + id);
        var info = $(input).attr("info");
        var divContent = $('#divCheckbox_' + name + '_' + violationCount + '_' + info);
        var disciplinaryRequestId;
        <g:if test="${disciplinaryRequest?.id}">
        disciplinaryRequestId = "${disciplinaryRequest?.id}";
        </g:if>
        if (input.is(":checked")) {
            $.ajax({
                url: '${createLink(controller: 'disciplinaryRequest',action: 'getDisciplinaryJudgmentsInputs')}',
                type: 'POST',
                data: {
                    disciplinaryJudgmentId: info,
                    disciplinaryRequestId: disciplinaryRequestId,
                    violationCount: violationCount
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
                    var newDivContent = $(divContent).find("#newDivContent_" + info);
                    if (newDivContent) {
                        newDivContent.remove();
                    }
                    divContent.append(html);
                    gui.autocomplete.initialize($(divContent));
                    gui.dateTimePickers.initialize($(divContent));
                }
            });
        } else {
            var newDivContent = $(divContent).find("#newDivContent_" + info);
            if (newDivContent) {
                newDivContent.remove();
            }
        }
    }

    function viewEmployeeViolation() {

        %{--employeeViolationIdsList = [];--}%
        %{--var valueArray = [];--}%
        %{--$("input[name*='hiddenEmployeeViolation']").each(function () {--}%
        %{--valueArray = $(this).val();--}%
        %{--employeeViolationIdsList.push(valueArray.slice(1, -1));--}%
        %{--});--}%

        %{--var link = "${createLink(controller: 'employeeViolation',action: 'listModal')}?employeeId=${disciplinaryRequest?.employee?.id}&excludedIds=" + employeeViolationIdsList;--}%
        %{--var disciplinaryCategoryFormId = $('#disciplinaryCategoryFormId').val();--}%
        %{--if (disciplinaryCategoryFormId) {--}%
        %{--link = link + "&disciplinaryCategoryId=" + disciplinaryCategoryFormId;--}%
        %{--}--}%
        %{--$('#employeeViolationLink').attr("href", link);--}%
        %{--$('#employeeViolationLink').click();--}%

        $('#disciplinaryCategoryId').val($('#disciplinaryCategoryFormId').val());
        _dataTables['employeeViolationTable'].draw();
        $('#employeeViolations').show();
    }

    /*    $("#disciplinaryCategoryFormId").on("select2:close", function (e) {
     getViolationsWithJudgments();
     });*/

    <g:if test="${disciplinaryRequest?.id}">
    $(document).ready(function () {
        getViolationsWithJudgments();
    });
    </g:if>
</script>