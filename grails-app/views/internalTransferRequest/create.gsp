<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'InternalTransferRequest List')}" />
    <title>${title}</title>
    <script>
        function successCallBack(json) {
            if (json.success) {
                window.location.href = "${createLink(controller: 'internalTransferRequest',action: 'createNewInternalTransferRequest')}?employeeId=" + json.employeeId;
            }
        }
    </script>
</head>
<body>

<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'internalTransferRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>

            <msg:warning label="${message(code:'request.justCommittedEmployee.label')}" />


            <el:validatableResetForm name="internalTransferRequestForm" callBackFunction="successCallBack"
                                     controller="internalTransferRequest" action="selectEmployee">

                <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                               name                  : 'employeeId',
                                                               id                    : 'employeeId',
                                                               paramsGenerateFunction: 'employeeParams',
                                                               size                  :  6]"/>

                <el:formButton functionName="select" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
<script>

    /**
     * to get only employee with status COMMITTED
     */
    function employeeParams() {
        var searchParams = {};
        searchParams.categoryStatusId = "${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.toString()}";
        return searchParams;
    }

</script>

</body>
</html>
