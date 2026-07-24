package org.openedx.core.domain.model

import org.openedx.core.BlockType

/**
 * Assignment subsections with library_content wrappers may not roll up completion on the LMS
 * blocks API. Treat complete when all descendant problems are done.
 */
fun Block.isAssignmentCompleted(allBlocks: List<Block>): Boolean {
    if (isCompleted()) return true

    val problems = collectDescendantBlocks(allBlocks).filter { it.type == BlockType.PROBLEM }
    return problems.isNotEmpty() && problems.all { it.completion == 1.0 }
}

fun Block.assignmentCompletion(allBlocks: List<Block>): Double {
    if (isCompleted()) return 1.0

    val problems = collectDescendantBlocks(allBlocks).filter { it.type == BlockType.PROBLEM }
    return if (problems.isEmpty()) {
        completion
    } else {
        problems.count { it.completion == 1.0 }.toDouble() / problems.size
    }
}

/**
 * Prefer LMS course_progress (Sumac-style). Fall back to MCQ rollup when LMS total is missing.
 */
fun assignmentProgress(courseStructure: CourseStructure, assignments: List<Block>): Progress {
    val lmsProgress = courseStructure.progress
    return if (lmsProgress != null && lmsProgress.total > 0) {
        lmsProgress
    } else {
        val blockData = courseStructure.blockData
        Progress(
            completed = assignments.count { it.isAssignmentCompleted(blockData) },
            total = assignments.size,
        )
    }
}

private fun Block.collectDescendantBlocks(allBlocks: List<Block>): List<Block> {
    if (descendants.isEmpty() || allBlocks.isEmpty()) return emptyList()

    val blockMap = allBlocks.associateBy { it.id }
    val result = mutableListOf<Block>()

    fun walk(blockId: String) {
        val block = blockMap[blockId] ?: return
        result.add(block)
        block.descendants.forEach { walk(it) }
    }

    descendants.forEach { walk(it) }
    return result
}
