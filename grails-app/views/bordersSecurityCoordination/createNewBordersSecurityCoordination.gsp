<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'bordersSecurityCoordination.entity', default: 'bordersSecurityCoordination List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'bordersSecurityCoordination List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'bordersSecurityCoordination', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="bordersSecurityCoordinationForm"
                                     callLoadingFunction="performPostActionWithEncodedId"
                                     controller="bordersSecurityCoordination" action="save">
                <g:render template="/bordersSecurityCoordination/form"
                          model="[bordersSecurityCoordination: bordersSecurityCoordination]"/>
                <el:formButton functionName="saveAndContinueButton" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel"
                               onClick="window.location.href='${createLink(controller: 'bordersSecurityCoordination', action: 'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>

<script>
    $(document).ready(function () {
        $(window).load(function () {

            /**
             * set contact type to شخصي
             */
            var newOption = new Option("شخصي", "1", true, true);
            $('#contactTypeId').append(newOption);
            $('#contactTypeId').trigger('change');

            /**
             * set contact method to  اخر عنوان
             */
            var newOption = new Option("عنوان اخر", "4", true, true);
            $('#contactMethodId').append(newOption);
            $('#contactMethodId').trigger('change');

            $("#relatedObjectDiv").remove();

        });
    });
</script>

</body>
</html>
