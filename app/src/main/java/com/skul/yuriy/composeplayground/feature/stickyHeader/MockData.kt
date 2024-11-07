package com.skul.yuriy.composeplayground.feature.stickyHeader

fun generateSections(): List<Section> {
    return listOf(
        Section(
            header = "Introduction",
            list = listOf(
                Item(text = "Lorem ipsum dolor sit amet"),
                Item(text = "Consectetur adipiscing elit"),
                Item(text = "Sed do eiusmod tempor incididunt"),
                Item(text ="Ut labore et dolore magna aliqua"),
                Item(text ="Ut enim ad minim veniam"),
                Item(text ="Quis nostrud exercitation ullamco"),
                Item(text ="Laboris nisi ut aliquip ex ea commodo consequat"),
                Item(text ="Duis aute irure dolor in reprehenderit"),
                Item(text ="In voluptate velit esse cillum dolore")
            ),
            isExpanded = true
        ),
        Section(
            header = "Chapter 1",
            list = listOf(
                Item(text ="Quis autem vel eum iure reprehenderit"),
                Item(text ="Qui in ea voluptate velit esse"),
                Item(text ="Quam nihil molestiae consequatur"),
                Item(text ="Vel illum qui dolorem eum fugiat"),
                Item(text ="Ut aut reiciendis voluptatibus maiores"),
                Item(text ="Alias consequatur aut perferendis doloribus"),
                Item(text ="Asperiores repellat")
            ),
            isExpanded = true
        ),
        Section(
            header = "Chapter 2",
            list = listOf(
                Item(text ="Ut enim ad minima veniam"),
                Item(text ="Quis nostrum exercitationem ullam corporis suscipit"),
                Item(text ="Laboriosam, nisi ut aliquid ex ea commodi consequatur"),
                Item(text ="Quis autem vel eum iure reprehenderit"),
                Item(text ="Qui in ea voluptate velit esse quam nihil molestiae"),
                Item(text ="Vel illum qui dolorem eum fugiat quo voluptas nulla pariatur")
            ),
            isExpanded = true
        ),
        Section(
            header = "Chapter 3",
            list = listOf(
                Item(text ="At vero eos et accusamus et iusto odio"),
                Item(text ="Dignissimos ducimus qui blanditiis praesentium"),
                Item(text ="Voluptatum deleniti atque corrupti quos dolores"),
                Item(text ="Et quas molestias excepturi sint occaecati"),
                Item(text ="Cupiditate non provident"),
                Item(text ="Similique sunt in culpa qui officia deserunt mollitia animi")
            ),
            isExpanded = true
        ),
        Section(
            header = "Chapter 4",
            list = listOf(
                Item(text ="Excepteur sint occaecat cupidatat non proident"),
                Item(text ="Sunt in culpa qui officia deserunt mollit anim id est laborum"),
                Item(text ="Sed ut perspiciatis unde omnis iste natus error sit voluptatem"),
                Item(text ="Accusantium doloremque laudantium"),
                Item(text ="Totam rem aperiam"),
                Item(text ="Eaque ipsa quae ab illo inventore veritatis et quasi"),
                Item(text ="Architecto beatae vitae dicta sunt explicabo")
            ),
            isExpanded = true
        ),
        Section(
            header = "Chapter 5",
            list = listOf(
                Item(text ="Nemo enim ipsam voluptatem quia voluptas sit"),
                Item(text ="Aspernatur aut odit aut fugit, sed quia consequuntur"),
                Item(text ="Magni dolores eos qui ratione voluptatem sequi nesciunt"),
                Item(text ="Neque porro quisquam est, qui dolorem ipsum quia dolor"),
                Item(text ="Sit amet, consectetur, adipisci velit"),
                Item(text ="Sed quia non numquam eius modi tempora incidunt"),
                Item(text ="Ut labore et dolore magnam aliquam quaerat voluptatem"),
                Item(text ="Ut enim ad minima veniam, quis nostrum exercitationem"),
                Item(text ="Ullam corporis suscipit laboriosam")
            ),
            isExpanded = true
        ),
        Section(
            header = "Conclusion",
            list = listOf(
                Item(text ="Nam libero tempore, cum soluta nobis est eligendi"),
                Item(text ="Optio cumque nihil impedit quo minus id quod maxime placeat"),
                Item(text ="Facere possimus, omnis voluptas assumenda est"),
                Item(text ="Omnis dolor repellendus"),
                Item(text ="Temporibus autem quibusdam et aut officiis debitis"),
                Item(text ="Aut rerum necessitatibus saepe eveniet"),
                Item(text ="Ut et voluptates repudiandae sint et molestiae non recusandae"),
                Item(text ="Itaque earum rerum hic tenetur a sapiente delectus"),
                Item(text ="Ut aut reiciendis voluptatibus maiores alias consequatur"),
                Item(text ="Aut perferendis doloribus asperiores repellat")
            ),
            isExpanded = true
        ),
        Section(
            header = "Appendix A",
            list = listOf(
                Item(text ="Lorem ipsum dolor sit amet"),
                Item(text ="Consectetur adipiscing elit"),
                Item(text ="Sed do eiusmod tempor incididunt ut labore"),
                Item(text ="Et dolore magna aliqua"),
                Item(text ="Ut enim ad minim veniam, quis nostrud exercitation"),
                Item(text ="Laboris nisi ut aliquip ex ea commodo consequat"),
                Item(text ="Duis aute irure dolor in reprehenderit"),
                Item(text ="In voluptate velit esse cillum dolore eu fugiat nulla pariatur"),
                Item(text ="Excepteur sint occaecat cupidatat non proident")
            ),
            isExpanded = true
        ),
        Section(
            header = "Appendix B",
            list = listOf(
                Item(text ="Sunt in culpa qui officia deserunt mollit anim id est laborum"),
                Item(text ="Sed ut perspiciatis unde omnis iste natus error sit voluptatem"),
                Item(text ="Accusantium doloremque laudantium"),
                Item(text ="Totam rem aperiam"),
                Item(text ="Eaque ipsa quae ab illo inventore veritatis et quasi architecto"),
                Item(text ="Beatae vitae dicta sunt explicabo"),
                Item(text ="Nemo enim ipsam voluptatem quia voluptas"),
                Item(text ="Aspernatur aut odit aut fugit")
            ),
            isExpanded = true
        ),
        Section(
            header = "References",
            list = listOf(
                Item(text ="Qui dolorem ipsum quia dolor sit amet"),
                Item(text ="Neque porro quisquam est"),
                Item(text ="Consectetur adipisci velit"),
                Item(text ="Sed quia non numquam eius modi tempora incidunt"),
                Item(text ="Ut labore et dolore magnam aliquam quaerat voluptatem"),
                Item(text ="Ut enim ad minima veniam, quis nostrum exercitationem")
            ),
            isExpanded = true
        )
    )
}

sealed class UiStateStickyScreen {
    data class Data(val data: List<Section>) : UiStateStickyScreen()
}
