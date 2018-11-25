<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'disciplinaryRequest.entity', default: 'DisciplinaryRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.edit.label', args: [entity], default: 'DisciplinaryRequest List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <el:modalLink
                    link="${createLink(controller: 'disciplinaryRequest', action: 'previousJudgmentsModal', id: disciplinaryRequest?.employee?.id)}"
                    preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                    label="${message(code: 'disciplinaryRequest.previousViolationsAndJudgments.label')}">
                <i class="icon-list"></i>
            </el:modalLink>
            <btn:listButton style="margin-right: 10px;"
                            onClick="window.location.href='${createLink(controller: 'disciplinaryRequest', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm callBackBeforeSendFunction="addInputsValidation" name="disciplinaryRequestForm"
                                controller="disciplinaryRequest" action="update">
                <g:render template="/disciplinaryRequest/form" model="[disciplinaryRequest: disciplinaryRequest]"/>
                <el:hiddenField name="id" value="${disciplinaryRequest?.id}"/>
                <el:formButton isSubmit="true"  functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
<script>
    function addInputsValidation() {
        $("input[name*='value'],select[name*='currencyId'],select[name*='unitId']").each(function () {
            var id = $(this).attr("id");
            if($(this).hasClass("isRequired")) {
                gui.formValidatable.addRequiredField('disciplinaryRequestForm', id);
            }
        });
        return gui.formValidatable.validate('disciplinaryRequestForm');
    }
</script>
</body>
</html>