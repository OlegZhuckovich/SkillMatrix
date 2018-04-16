$(document).ready(function() {

    var url = "http://localhost:8000/controller";

    $.ajax({
        url: url,
        method: "get",
        data: {
            "command": "get_data"
        },
        success: function(data) {
            var mainList = $('#mainList');
            mainList.empty();
            mainList.append(data);
        }
    });

    $("#searchField").keyup(function () {
        var filter = $(this).val();
        $("li").each(function () {
            if (filter == "") {
                $(this).css("visibility", "visible");
                $(this).fadeIn();
            } else if ($(this).text().search(new RegExp(filter, "i")) < 0) {
                if(!$(this).parent().is('li')){
                    $(this).css("visibility", "hidden");
                    $(this).fadeOut();
                }
            } else {
                $(this).css("visibility", "visible");
                $(this).fadeIn();
            }
        });
    });
});
