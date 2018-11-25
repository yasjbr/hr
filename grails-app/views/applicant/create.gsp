<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'Applicant List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'Applicant List')}"/>
    <title>${title}</title>
</head>
<body>
<script>
    function successCallBack(json) {
        if(json.success){
            window.location.href = "${createLink(controller: 'applicant',action: 'createNewApplicant')}?personId="+json.personId;
        }
    }
    function confirmAddPerson() {
        gui.confirm.confirmFunc("${message(code: 'person.addNew.confirm.title')}", "${message(code: 'person.addNew.confirm.message')}", function () {
            window.location.href='${createLink(controller: 'applicant', action: 'createNewPerson')}';
        });
    }
    <g:if test="${params.boolean("anyApplicantExist")}">
    $(window).on('load', function() {
        $("#personApplicantProfilesModal")[0].click();
    });
    </g:if>
</script>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <el:modalLink link="${createLink(controller: 'applicant',action: 'personApplicantProfilesModal',params: [personId: params.personId,anyOpenApplicantExist: params.anyOpenApplicantExist])}"
                          preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135" id="personApplicantProfilesModal" style="display: none;"
                          label="">
            </el:modalLink>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'applicant', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm callBackFunction="successCallBack" name="applicantForm" controller="applicant" action="selectPerson">
                <el:formGroup>
                    <el:autocomplete
                            optionKey="id"
                            optionValue="name"
                            size="6"
                            class=" isRequired"
                            controller="person"
                            action="autocomplete"
                            name="personId"
                            label="${message(code: 'applicant.searchPerson.label', default: 'searchPerson')}"/>
                </el:formGroup>
                <el:formButton functionName="select" isSubmit="true" />
                <el:formButton functionName="addButton" onclick="confirmAddPerson()" />
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>


