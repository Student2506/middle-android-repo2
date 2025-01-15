import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send message should update messages with MyMessage`() = runTest {
        val message = Message.MyMessage("TestMessage")
        viewModel.sendMyMessage(message = message)
        assert(viewModel.messages.value.messages.contains(Message.MyMessage("TestMessage")))
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val scope = CoroutineScope(Job() + testDispatcher)
        val jobs = mutableListOf<Job>()
        Dispatchers.setMain(testDispatcher)
        try {
            messagesToSend.forEach { message ->
                val job = scope.launch {
                    viewModel.sendMyMessage(message)
                }
                jobs.add(job)
            }
            jobs.joinAll()
        } finally {
            Dispatchers.resetMain()
        }
        assert(viewModel.messages.value.messages.size == 100)
        viewModel.messages.value.messages.zip(messagesToSend)
            .forEach { (messageInList, messageOriginal) ->
                assert(messageOriginal.text == (messageInList as Message.MyMessage).text)
            }
    }
}