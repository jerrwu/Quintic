package com.jerrwu.quintic.entities.cell

class CellEntity(
    var text: String?,
    var number: Int?
) {
    constructor(
        text: String?
    ): this(text, 0)
}