package com.stxx.wyhvisitorandroid.bean

/**
 * description:
 * Created by liNan on  2020/4/22 10:40
 */
data class BDasrResp(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val scope: String,
    val session_key: String,
    val session_secret: String
)

/**
 * 对话
 */
data class BdBotResult(
    val error_code: Int,
    val result: Result
)

data class Result(
    val dialog_state: DialogState,
    val interaction_id: String,
    val log_id: String?,
    val response_list: List<Response>,
    val service_id: String?,
    val session_id: String,
    val timestamp: String,
    val version: String
)

data class DialogState(
    val contexts: Contexts,
    val skill_states: SkillStates
)

class Contexts(
)

class SkillStates(
)

data class Response(
    val action_list: List<Action>,
    val msg: String,
    val origin: String,
    val qu_res: QuRes,
    val schema: Schema,
    val status: Int
)

data class Action(
    val action_id: String,
    val confidence: Double,
    val custom_reply: String,
    val refine_detail: RefineDetail,
    val say: String,
    val type: String
)

data class RefineDetail(
    val clarify_reason: String,
    val interact: String,
    val option_list: List<Any>
)

data class QuRes(
    val candidates: List<Any>,
    val lexical_analysis: List<Any>,
    val qu_res_chosen: String,
    val raw_query: String,
    val sentiment_analysis: SentimentAnalysis,
    val status: Int,
    val timestamp: Int
)

data class SentimentAnalysis(
    val label: String,
    val pval: Double
)

data class Schema(
    val domain_confidence: Double,
    val intent: String,
    val intent_confidence: Double,
    val slots: List<Slot>,
    val slu_tags: List<Any>
)

data class Slot(
    val begin: Int,
    val confidence: Double,
    val length: Int,
    val merge_method: String,
    val name: String,
    val normalized_word: String,
    val original_word: String,
    val session_offset: Int,
    val sub_slots: List<Any>,
    val word_type: String
)