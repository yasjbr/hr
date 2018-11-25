<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'employee.entity', default: 'Employee List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'Employee List')}"/>
    <title>${title}</title>

    <script>
        function successCallBack(json) {
            if (json.success) {
                if (json.firmEncodedId) {
                    window.location.href = "${createLink(controller: 'employee',action: 'createNewEmployee')}?personId=" + json.personId + "&firmId=" + json.firmEncodedId;
                } else {
                    window.location.href = "${createLink(controller: 'employee',action: 'createNewEmployee')}?personId=" + json.personId;
                }
            }
        }

        function confirmAddPerson() {
            gui.confirm.confirmFunc("${message(code: 'person.addNew.confirm.title')}", "${message(code: 'person.addNew.confirm.message')}", function () {
                window.location.href = '${createLink(controller: 'employee', action: 'createNewPerson')}';
            });
        }


        /**
         * to get only firms not centralized with AOC
         */
        function firmParams() {
            var searchParams = {};
            searchParams.centralizedWithAOC = "false";
            return searchParams;
        }


    </script>
</head>

<body>

<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'employee', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm callBackFunction="successCallBack" name="employeeForm" controller="employee"
                                     action="getPerson">

                %{--<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">--}%
                    <el:formGroup>
                        <el:autocomplete
                                optionKey="id"
                                optionValue="name"
                                size="8"
                                class=" isRequired"
                                controller="firm"
                                action="autocomplete"
                                name="firm.id"
                                paramsGenerateFunction="firmParams"
                                label="${message(code: 'firm.label', default: 'firm')}"/>
                    </el:formGroup>
                %{--</sec:ifAnyGranted>--}%

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
                </el:formGroup>
                <el:formButton functionName="select" isSubmit="true"/>
                <el:formButton functionName="addButton" onclick="confirmAddPerson()"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>

</body>
</html>