<script>

    var mandatoryInspection = [];

    function InspectionCategoriesParams() {
        var searchParams = {};
        searchParams.isRequiredByFirmPolicy = false;
        searchParams.allInspectionCategory = true;
        return searchParams;
    }


    function removeCloseBtn() {
        var selectionRendered = $(".inspectionCategoriesDiv").find(".select2-selection__rendered");
        for (var count = 0; count < mandatoryInspection.length; count++) {
            var li = selectionRendered.find("li[title='" + mandatoryInspection[count] + "']");
            li.addClass("mandatoryInspection");
            li.css("background-color", "rgba(32, 98, 213, 0.34)");
            li.find("span").remove();
        }
        if (mandatoryInspection.length > 0) {
            selectionRendered.find(".select2-selection__clear").remove();
        }
    }

    /*to get mandatory inspection*/
    $(document).ready(function () {
        $(window).load(function () {
            $(".profile-info-name").css('border', '1px solid #f7fbff');
            var $el = $("#inspectionCategories");
            var selectionRendered = $(".inspectionCategoriesDiv").find(".select2-selection__rendered");

            $.ajax({
                url: "<g:createLink controller="Job" action="getMandatoryInspection"/>",
                type: 'POST',
                beforeSend: function () {
                },
                complete: function () {
                },
                success: function (data) {

                    for (var i = 0; i < data.length; i++) {
                        var text = data[i].text + " (إلزامي) ";
                        var id = data[i].id;
                        mandatoryInspection.push(text);
                        var newOption = new Option(text, id, true, true);
                        $el.append(newOption);
                        $el.trigger('change');
                    }
                    removeCloseBtn();
                },
                error: function (request, status, error) {

                }
            });


            $el.on('select2:select', function (evt) {
                removeCloseBtn();
            });
            $el.on('select2:selecting', function (evt) {
                removeCloseBtn();
            });
            $el.on('change', function (evt) {
                removeCloseBtn();
            });
            $el.on('select2:loaded', function (evt) {
                removeCloseBtn();
            });
            $el.on('select2:removed', function (evt) {
                removeCloseBtn();
            });
            $el.on('select2:open', function (evt) {
                removeCloseBtn();
            });
        });
    });

    $("#inspectionCategories").on('select2:select', function (evt) {
        removeCloseBtn();
    });
    $("#inspectionCategories").on('select2:selecting', function (evt) {
        removeCloseBtn();
    });
    $("#inspectionCategories").on('change', function (evt) {
        removeCloseBtn();
    });
    $("#inspectionCategories").on('select2:loaded', function (evt) {
        removeCloseBtn();
    });
    $("#inspectionCategories").on('select2:removed', function (evt) {
        removeCloseBtn();
    });
    $("#inspectionCategories").on('select2:open', function (evt) {
        removeCloseBtn();
    });
</script>